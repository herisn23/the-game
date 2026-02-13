package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import org.roldy.core.DayNightCycle
import org.roldy.core.Diagnostics
import org.roldy.core.biome.toBiomes
import org.roldy.core.camera.OffsetShiftingManager
import org.roldy.core.camera.SimpleThirdPersonCamera
import org.roldy.core.configuration.loadBiomesConfiguration
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.keyLeft
import org.roldy.core.keyRight
import org.roldy.core.map.MapData
import org.roldy.core.map.MapGenerator
import org.roldy.core.map.MapSize
import org.roldy.core.map.findFlatAreas
import org.roldy.core.model.loadModelInstances
import org.roldy.core.postprocess.PostProcessing
import org.roldy.core.shader.UniformAttribute
import org.roldy.core.shader.foliageShaderProvider
import org.roldy.core.shader.shiftingShaderProvider
import org.roldy.core.system.ShadowSystem
import org.roldy.core.system.WindSystem
import org.roldy.core.utils.invoke
import org.roldy.g3d.AssetManagersLoader
import org.roldy.g3d.environment.*
import org.roldy.g3d.pawn.CharacterController
import org.roldy.g3d.pawn.PawnManager
import org.roldy.g3d.pawn.PawnModelBuilder
import org.roldy.g3d.pawn.PawnRenderer
import org.roldy.g3d.skybox.Skybox
import org.roldy.g3d.terrain.Terrain
import org.roldy.g3d.terrain.TerrainSampler


/**
 * LOD HANDLING
 *
 * // In your render loop
 * float distance = camera.position.dst(objectPosition);
 *
 * if (distance < 20f) {
 *     currentInstance = new ModelInstance(modelHigh);
 * } else if (distance < 50f) {
 *     currentInstance = new ModelInstance(modelMed);
 * } else {
 *     currentInstance = new ModelInstance(modelLow);
 * }
 *
 *
 *
 */


class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {


    val emissive by disposable { EnvTexturesAssetAssetManager.tropicalEmissive01.get() }
    val diffuse by disposable { EnvTexturesAssetAssetManager.tropicalTexture01.get() }

    //    val materials by lazy {
//        loadMaterials(EnvTexturesAssetAssetManager.textureMap)
//    }
    val materials by lazy {
        loadModelInstances(EnvTexturesAssetAssetManager.textureMap)
    }


    val windSystem = WindSystem()
    val mapSizeScale = 1
    val mapSizeLength = 1024
    val mapSize = MapSize(mapSizeLength * mapSizeScale, mapSizeLength * mapSizeScale)
    val mapData = MapData(1, mapSize)
    val biomes by lazy { loadBiomesConfiguration().toBiomes() }
    var mapTerrainData = MapGenerator(mapData, biomes).generate()
    val offsetShiftingManager = OffsetShiftingManager()

    var terrainInstance = Terrain(mapTerrainData, offsetShiftingManager, mapSize)
    val heightSampler = TerrainSampler(
        noiseData = terrainInstance.mapTerrainData.noiseData,
        heightScale = terrainInstance.heightScale,
        width = terrainInstance.width,
        depth = terrainInstance.depth,
        scale = terrainInstance.scale
    ).apply {
        offsetShiftingManager.shiftListeners.add { _, _, totalOffset ->
            originOffset = totalOffset
        }
    }
    val shadowSystem by disposable {
        ShadowSystem(
            offsetShiftingManager,
            camera,
            windSystem = windSystem
        )
    }
    val cameraController by lazy {
        SimpleThirdPersonCamera(camera, heightSampler).apply {
            onZoomChanged = shadowSystem::updateShadowDistance
        }
    }


    val modelBuilder by disposable(::PawnModelBuilder)

    val character by disposable {
        PawnManager(modelBuilder).apply {
            cycleSets()
        }.run {
            PawnRenderer(this)
        }
    }
    val charController by lazy {
        CharacterController(character.manager.instance, heightSampler, cameraController).apply {
            offsetShiftingManager.shiftListeners.add { shiftX, shiftZ, _ ->
                onOriginShift(shiftX, shiftZ)
            }
            val scale = terrainInstance.scale
            val area = mapTerrainData.noiseData.findFlatAreas(1).first()
            val corX = (mapSize.width / 2f)
            val corZ = (mapSize.height / 2f)
// Character position
            val charX = (area.center.x - corX) * scale// Apply terrain offset!
            val charZ = (area.center.y - corZ) * scale

            initializeAt(charX, charZ)


            fun ModelInstance.position(ox: Float, oz: Float) {
                transform.idt()
                val tx = charX + ox
                val tz = charZ + oz
                val ty = heightSampler.getHeightAt(tx, tz)
                transform.setTranslation(tx, ty, tz)
            }

            tropicalModel.position(5f, 5f)
            grass.position(1f, 1f)
            tree.position(10f, 10f)
            palm.position(-2f, -2f)
        }
    }

    val tropicalModel by lazy {
        TropicalAssetManager.bldGiantColumn01.property(diffuse, emissive).apply {
            transform.idt()
            transform.setTranslation(0f, 0f, 0f)
        }
    }
    val grass by lazy {
        TropicalAssetManager.envGrassMedClump01.simpleFoliage(materials.first { it.id == "Grass_Med_Mat_01" })
            .apply {
                nodes.first().children.removeAll {
                    !it.id.contains("LOD0")
                }
            }
    }
    val tree by lazy {
        val b = materials.first { it.id == "Branches_01" }
        val l = materials.first { it.id == "Tree_Mat_02" }
        val u = l.get(UniformAttribute::class.java, UniformAttribute.uniformsAttr).uniforms
        u.keys.sorted().forEach {
            println(u.getValue(it))
        }
        TropicalAssetManager.envTreeForest02.tree(b, l)
            .apply {
                nodes.first().children.removeAll {
                    !it.id.contains("LOD0")
                }
            }
    }
    val palm by lazy {
        TropicalAssetManager.envTreePalm01.simpleFoliage(materials.first { it.id == "Palm_Mat_01" })
            .apply {
                nodes.first().children.removeAll {
                    !it.id.contains("LOD0")
                }
            }
    }




    var loading = true
    val postProcess = PostProcessing()
    val diagnostics by disposable { Diagnostics() }

    init {
        Diagnostics.addProvider { "Chunks: ${terrainInstance.getVisibleCount(camera)} / ${terrainInstance.getTotalCount()}" }
    }


    val envModelBatch by disposable { ModelBatch(shiftingShaderProvider(offsetShiftingManager)) }
    val foliageBatch by disposable { ModelBatch(foliageShaderProvider(offsetShiftingManager, windSystem)) }

    val sun by disposable { SunBillboard(camera, shadowSystem.shadowLight) }
    val dayCycle = DayNightCycle(shadowSystem.environment, shadowSystem.shadowLight)

    val skybox by lazy { Skybox(dayCycle) }


    //    val controller by lazy { ModelController(character.manager.instance, camera) }
    val adapter by lazy {
        InputMultiplexer().apply {
//            addProcessor(RTSInputHandler(camera, TerrainRaycaster(heightSampler, camera), charController))
            addProcessor(cameraController)
        }
            .also(Gdx.input::setInputProcessor)
    }

    override fun resize(width: Int, height: Int) {
        postProcess.resize(width, height)
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }
    val spriteBatch: SpriteBatch = SpriteBatch()
    override fun render(delta: Float) {

        if (loading && AssetManagersLoader.update()) {
            loading = false
            adapter
            materials
        }
        if (loading) return

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            postProcess.toggle()
        }
        if (Gdx.input.isKeyPressed(keyLeft)) {
            dayCycle.update(-delta * 100)
        }
        if (Gdx.input.isKeyPressed(keyRight)) {
            dayCycle.update(delta * 100)
        }


        context(delta, camera) {
            offsetShiftingManager.update(character.manager.instance)
            windSystem.update()
            camera.update()
            charController.update()

//            dayCycle.update(delta)

            shadowSystem {
                render(grass)
                render(tree)
                render(palm)
                render(tropicalModel)
                render(character.manager.instance)
            }

            postProcess {
                skybox.render()
                sun.render()
                context(shadowSystem.environment) {
                    terrainInstance.render()
                    envModelBatch {
                        listOf(tropicalModel)
                    }

                    foliageBatch {
                        listOf(grass, tree, palm)
                    }
                    character.render()
                }
            }
            diagnostics.render()
        }
    }


}

