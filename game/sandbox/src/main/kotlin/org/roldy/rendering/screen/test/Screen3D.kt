package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Diagnostics
import org.roldy.core.biome.toBiomes
import org.roldy.core.camera.OffsetShiftingManager
import org.roldy.core.camera.SimpleThirdPersonCamera
import org.roldy.core.collision.CollisionSystem
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
import org.roldy.core.system.ShadowSystem
import org.roldy.core.system.WindSystem
import org.roldy.g3d.AssetManagersLoader
import org.roldy.g3d.environment.*
import org.roldy.g3d.environment.decal.createPlanetsBillboards
import org.roldy.g3d.pawn.CharacterController
import org.roldy.g3d.pawn.PawnManager
import org.roldy.g3d.pawn.PawnModelBuilder
import org.roldy.g3d.pawn.PawnRenderer
import org.roldy.g3d.skybox.Skybox
import org.roldy.g3d.terrain.Terrain
import org.roldy.g3d.terrain.TerrainSampler
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {

    val instances by lazy {
        loadModelInstances(EnvTexturesAssetAssetManager.textureMap).associateBy {
            it.modelName
        }
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
        CharacterController(character.manager.instance, heightSampler, cameraController, collisionSystem).apply {
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


            fun EnvModelInstance.position(ox: Float, oz: Float) {
                val tx = charX + ox
                val tz = charZ + oz
                val ty = heightSampler.getHeightAt(tx, tz)
                transform.setTranslation(tx, ty, tz)
            }

            val radius = 80f          // scatter area radius
            val random = Random(42)   // fixed seed for reproducibility

            val placed = mutableListOf<Vector2>()

            fun List<EnvModelInstance>.place(minDistance: Float, minRadius: Float = 2f) {
                forEach { instance ->
                    var x: Float
                    var z: Float
                    var attempts = 0

                    do {
                        val angle = random.nextFloat() * MathUtils.PI2
                        val dist = minRadius + random.nextFloat() * (radius - minRadius)
                        x = cos(angle) * dist
                        z = sin(angle) * dist
                        attempts++
                    } while (attempts < 30 && placed.any { it.dst(x, z) < minDistance })

                    placed.add(Vector2(x, z))

                    val rotY = random.nextFloat() * 360f

                    instance.position(x, z)
                    instance.transform.setRotation(0f, rotY, 0f)
                    instance.transform.apply()
                }
            }

            grasses.place(0.1f)
            trees.place(1f)
            staticModels.forEach {
                it.position(0f, 0f)
                it.transform.apply()
            }
        }
    }
    val grasses by lazy {
        val grasses = listOf(
            "SM_Env_Grass_Tall_Clump_01",
            "SM_Env_Grass_Tall_Clump_02",
            "SM_Env_Grass_Tall_Clump_03",
            "SM_Env_Grass_Tall_Plane_01",
            "SM_Env_Ground_Cover_01",
            "SM_Env_Ground_Cover_02",
            "SM_Env_Ground_Cover_03",
            "SM_Env_Bush_Palm_04",
            "SM_Env_Bush_Tropical_01",
            "SM_Env_Bush_Tropical_03"
        )
        val random = Random(42)
        (0..1000).mapNotNull {
            instances.getValue(grasses.random(random)).createInstance()
        }
    }
    val trees by lazy {
        val models = listOf(
            "SM_Env_Tree_Banana_01",
            "SM_Env_Tree_Banana_02",
            "SM_Env_Tree_Banana_03",
            "SM_Env_Tree_Forest_01",
            "SM_Env_Tree_Forest_02",
            "SM_Env_Tree_Forest_03",
            "SM_Env_Tree_Pohutukawa_01",
            "SM_Env_Tree_Pohutukawa_02",
            "SM_Env_Tree_Pohutukawa_03",
            "SM_Env_Tree_Pohutukawa_04"
        )
        val random = Random(42)
        (0..20).mapNotNull {
            instances.getValue(models.random(random)).createInstance()
        }

    }

    val foliageModels by lazy {
//        listOf(grass, tree, palm)
//        instances.filter { it.value.foliage }.values.toList()
//        listOf(instances.getValue("SM_Env_Bush_Tropical_03"))
        trees + grasses
    }
    val staticModels by lazy {
//        listOf(tropicalModel)
        listOfNotNull(instances.getValue("SM_Bld_Ruins_Archway_02").createInstance())
//        emptyList<EnvModelInstance>()
    }


    val collisionSystem = CollisionSystem(offsetShiftingManager) { foliageModels + staticModels }

    var loading = true
    val postProcess = PostProcessing()
    val diagnostics by disposable { Diagnostics() }

    init {
        Diagnostics.addProvider { "Chunks: ${terrainInstance.getVisibleCount(camera)} / ${terrainInstance.getTotalCount()}" }
        Diagnostics.addProvider { "Foliage: ${foliageBatch.currentRenderedModels} / ${foliageModels.size}" }
        Diagnostics.addProvider { "Collisions enabled: ${charController.checkCollision}" }
    }


    val staticBatch by disposable { staticModelRenderer(camera, offsetShiftingManager) { staticModels } }
    val foliageBatch by disposable { foliageModelRenderer(camera, windSystem, offsetShiftingManager) { foliageModels } }


    val dayCycle = createDayNightSystemInstance(
        shadowSystem.environment,
        shadowSystem.sunLight,
        shadowSystem.moonLight
    )
    val planets by disposable { createPlanetsBillboards(camera, dayCycle) }

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

    override fun render(delta: Float) {

        if (loading && AssetManagersLoader.update()) {
            loading = false
            adapter
        }
        if (loading) return

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            postProcess.toggle()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            charController.checkCollision = !charController.checkCollision
        }
        if (Gdx.input.isKeyPressed(keyLeft)) {
            context(-delta * 100) {
                dayCycle.update()
            }

        }
        if (Gdx.input.isKeyPressed(keyRight)) {
            context(delta * 100) {
                dayCycle.update()
            }
        }


        context(delta, camera) {
            windSystem.update()
            camera.update()
            charController.update()
            offsetShiftingManager.update(character.manager.instance)

            dayCycle.update()

            shadowSystem(
                {
                    with(staticBatch) {
                        renderShadows()
                    }
                    with(foliageBatch) {
                        renderShadows()
                    }
                    render(character.manager.instance)
                }
            ) {
                postProcess {
                    skybox.render()
                    planets.render()
                    terrainInstance.render()
                    staticBatch.render()
                    foliageBatch.render()
                    character.render()
                }
            }


            diagnostics.render()
        }
    }


}

