package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import org.roldy.core.DayNightCycle
import org.roldy.core.Diagnostics
import org.roldy.core.biome.toBiomes
import org.roldy.core.camera.OffsetShiftingManager
import org.roldy.core.camera.ThirdPersonCamera
import org.roldy.core.configuration.loadBiomesConfiguration
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.keyLeft
import org.roldy.core.keyRight
import org.roldy.core.map.MapData
import org.roldy.core.map.MapGenerator
import org.roldy.core.map.MapSize
import org.roldy.core.map.findFlatAreas
import org.roldy.core.postprocess.PostProcessing
import org.roldy.core.shader.shiftingShaderProvider
import org.roldy.core.shadow.ShadowSystem
import org.roldy.core.utils.invoke
import org.roldy.core.utils.sequencer
import org.roldy.g3d.AssetManagersLoader
import org.roldy.g3d.environment.SunBillboard
import org.roldy.g3d.environment.TropicalAssetManager
import org.roldy.g3d.environment.instance
import org.roldy.g3d.pawn.*
import org.roldy.g3d.skybox.Skybox
import org.roldy.g3d.terrain.Terrain
import org.roldy.g3d.terrain.TerrainHeightSampler
import org.roldy.g3d.terrain.TerrainRaycaster


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
    val emissive by disposable { TropicalAssetManager.emissiveTexture.get() }
    val diffuse by disposable { TropicalAssetManager.diffuseTexture.get() }
    val offsetShiftingManager = OffsetShiftingManager().apply {
        onShift = { shiftX, shiftZ, totalOffset ->
            // Update height sampler with total offset
            heightSampler.originOffset = totalOffset

            // Shift character controller positions
            charController.onOriginShift(shiftX, shiftZ)
        }
    }
    val tropicalModel by lazy {
        TropicalAssetManager.bldGiantColumn01.instance(diffuse, emissive)
    }


    var loading = true
    val postProcess = PostProcessing()
    val biomes by lazy { loadBiomesConfiguration().toBiomes() }
    val diagnostics by disposable { Diagnostics() }
    fun createTargetMarker(): ModelInstance {
        val modelBuilder = ModelBuilder()
        val material = Material(ColorAttribute.createDiffuse(Color.GREEN))
        val model = modelBuilder.createCylinder(
            0.5f, 0.1f, 0.5f, 16,
            material,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        return ModelInstance(model.disposable())
    }

    val targetMarker = createTargetMarker()

    init {
        camera.position.set(0f, 20f, 0f)
        camera.update()
        Diagnostics.addProvider { "Chunks: ${terrainInstance.getVisibleCount(camera)} / ${terrainInstance.getTotalCount()}" }
    }


    val shadowSystem = ShadowSystem(
        offsetShiftingManager,
        camera
    )
    val light = DirectionalLight().set(Color.WHITE, -0.5f, -1f, -0.3f)
    val ambientLight =
        ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f)//ColorAttribute.createAmbient(hex("ffffff"))
    val env by lazy {
        Environment().apply {
            set(ambientLight)
            add(light)
        }
    }


    val mapSizeScale = 1
    val mapSizeLength = 1024
    val mapSize = MapSize(mapSizeLength * mapSizeScale, mapSizeLength * mapSizeScale)
    val mapData = MapData(1, mapSize)
    var mapTerrainData = MapGenerator(mapData, biomes).generate()
    var terrainInstance = changeTerrain()
    val heightSampler = TerrainHeightSampler(
        noiseData = terrainInstance.mapTerrainData.noiseData,
        heightScale = terrainInstance.heightScale,
        width = terrainInstance.width,
        depth = terrainInstance.depth,
        scale = terrainInstance.scale
    )
    val charController by lazy {
        RTSCharacterController(character.manager.instance, heightSampler)
    }

    val envModelBatch by disposable { ModelBatch(shiftingShaderProvider(offsetShiftingManager)) }

    val sun by disposable { SunBillboard(camera, shadowSystem.shadowLight) }
    val dayCycle = DayNightCycle(shadowSystem.environment, shadowSystem.shadowLight)

    data class TData(
        val name: String,
        var value: Float = 0f
    )

    val flatRegionAmount = TData("flatRegionAmount", 0.6f)
    val mountainHeight = TData("mountainHeight", 0.9f)

    var currentData = flatRegionAmount

    val seq by sequencer {
        listOf(flatRegionAmount, mountainHeight)
    }

    fun changeTerrain(): Terrain {
        return Terrain(mapTerrainData, offsetShiftingManager, mapSize)
    }

    val sens = 0.01f
    fun modify(dir: Int) {
        currentData.value = (currentData.value + dir * sens).coerceIn(0f, 10f)
        println("Set ${currentData}")
    }

    fun changeTerrainData() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            currentData = seq.next()
            println("Modify ${currentData.name}")
        }
    }

    val skybox by lazy { Skybox(dayCycle) }
    val modelBuilder by disposable(::PawnModelBuilder)

    val character by disposable {
        val scale = terrainInstance.scale
        PawnManager(modelBuilder).apply {
            val s = 0.1f
            val area = mapTerrainData.noiseData.findFlatAreas(1).first()
            val corX = (mapSize.width / 2f)
            val corZ = (mapSize.height / 2f)
// Character position
            val charX = (area.center.x - corX) * scale// Apply terrain offset!
            val charY = area.elevation * terrainInstance.heightScale
            val charZ = (area.center.y - corZ) * scale
            println("Starting Y: $charY")

            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(charX, charY, charZ)
            instance.transform.set(initialTransform)


            tropicalModel.transform.idt()
            tropicalModel.transform.setTranslation(charX, charY, charZ)

            camera.position.set(
                charX,  // Behind
                charY,    // Above
                charZ
            )
            camera.lookAt(charX, charY + 2f, charZ)  // Look at character's head
            camera.update()
            cycleSets()
        }.run {
            PawnRenderer(this)
        }
    }

    val cameraController by lazy {
        ThirdPersonCamera(camera, character.manager.instance)
//        EditorCameraController(camera)
    }

    //    val controller by lazy { ModelController(character.manager.instance, camera) }
    val adapter by lazy {
        InputMultiplexer().apply {
            addProcessor(RTSInputHandler(camera, TerrainRaycaster(heightSampler, camera), charController))
            addProcessor(cameraController)
        }
            .also(Gdx.input::setInputProcessor)
    }

    fun printNodeHierarchy(node: Node, indent: String = "") {
        println("$indent${node.id}")
        for (child in node.children) {
            printNodeHierarchy(child, "$indent  ")
        }
    }

    override fun resize(width: Int, height: Int) {
        postProcess.resize(width, height)
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    val anims by sequencer {
        PawnAnimations[character.manager.bodyType].all.toList()
    }

    override fun render(delta: Float) {

        if (loading && AssetManagersLoader.update()) {
            loading = false
            adapter
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
            charController.update(delta)
            cameraController.update()
            camera.update()
            dayCycle.update(delta)

            shadowSystem {
                render(tropicalModel)
                render(character.manager.instance, false)
            }

// Reset GL state after shadow pass

            postProcess {
                skybox.render()
                sun.render()
                context(shadowSystem.environment) {
                    terrainInstance.render()
                    envModelBatch {
                        listOf(tropicalModel)
                    }
                    character.render()
                }
            }
            diagnostics.render()
        }
    }


}