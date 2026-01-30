package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import org.roldy.core.Diagnostics
import org.roldy.core.camera.OffsetShiftingManager
import org.roldy.core.camera.StrategyCameraController
import org.roldy.core.camera.ThirdPersonCamera
import org.roldy.core.coroutines.async
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.map.MapData
import org.roldy.core.map.MapGenerator
import org.roldy.core.map.MapSize
import org.roldy.core.map.findFlatAreas
import org.roldy.core.postprocess.PostProcessing
import org.roldy.core.utils.hex
import org.roldy.core.utils.sequencer
import org.roldy.g3d.pawn.*
import org.roldy.g3d.skybox.Skybox
import org.roldy.g3d.terrain.Terrain
import org.roldy.g3d.terrain.TerrainHeightSampler
import org.roldy.g3d.terrain.TerrainRaycaster
import kotlin.random.Random


class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {
    var loading = true
    val postProcess = PostProcessing()

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

    val light = DirectionalLight().set(hex("CF7A4F"), -1f, -0.8f, -0.2f)
    val ambientLight = ColorAttribute.createAmbient(hex("CF7A4F"))
    val env by lazy {
        Environment().apply {
            set(ambientLight)
            add(light)
        }
    }

    val mapSize = MapSize(2048, 2048)
    val mapData = MapData(1, mapSize)
    var mapTerrainData = MapGenerator(mapData).generate()
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
    val offsetShiftingManager = OffsetShiftingManager().apply {
        onShift = { shiftX, shiftZ, totalOffset ->
            // Update height sampler with total offset
            heightSampler.originOffset = totalOffset

            // Shift character controller positions
            charController.onOriginShift(shiftX, shiftZ)
            terrainInstance.originOffset = totalOffset
        }
    }

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
        return Terrain(mapTerrainData, light, ambientLight, camera, mapSize).apply {
//            setPosition(0f+(mapSize.width/2)*scale, 0f, 0f+(mapSize.height/2)*scale)
        }
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

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            modify(-1)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            modify(1)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            regenerate()
//            character.manager.animationController.setAnimation(anims.next().id, -1)
        }
    }

    fun regenerate() {
        async {
            mapTerrainData = MapGenerator(
                MapData(Random.nextInt().toLong(), mapSize),
                flatRegionAmount = flatRegionAmount.value,  // 0-1: how much of map is flat
                mountainHeight = mountainHeight.value,       // How tall mountains are

            ).generate()
            it {
                this.terrainInstance = changeTerrain()
            }
        }
    }


    val skybox by lazy { Skybox() }
    val modelBuilder by disposable(::PawnModelBuilder)

    val character by disposable {
        val scale = terrainInstance.scale
        PawnManager(modelBuilder).apply {
            val area = mapTerrainData.noiseData.findFlatAreas(1).first()
            val corX = (mapSize.width / 2f)
            val corZ = (mapSize.height / 2f)
// Character position
            val charX = (area.center.x - corX) * scale// Apply terrain offset!
            val charY = area.elevation * terrainInstance.heightScale
            val charZ = (area.center.y - corZ) * scale
            println("Starting Y: $charY")
// Set character transform
            instance.transform.idt()
            instance.transform.setTranslation(charX, charY, charZ)
            instance.transform.rotate(Vector3.Y, 90f)

            camera.position.set(
                charX,  // Behind
                charY,    // Above
                charZ
            )
            camera.lookAt(charX, charY + 2f, charZ)  // Look at character's head
            camera.update()
        }.run {
            PawnRenderer(this)
        }
    }

    val cameraController by lazy {
        StrategyCameraController(camera).apply {
            val bounds = 100000f
            setBounds(-bounds, bounds, -bounds, bounds)
//        setZoomLimits(5f, 40f)
//        setAngleLimits(25f, 75f)
            panSpeed = 1000f
//        smoothness = 8f
        }
        ThirdPersonCamera(camera, character.manager.instance)
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

        if (loading && PawnAssetManager.assetManager.update()) {
            loading = false
            adapter
        }
        if (loading) return

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            postProcess.toggle()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            Gdx.app.log("UV", "=== UV VALUES BEING SET ===")
            for (i in 0 until 4) {
                val uv = terrainInstance.materialUVs[i]
                Gdx.app.log("UV", "u_uv$i: offset(${uv.offset.x}, ${uv.offset.y}), scale(${uv.scale.x}, ${uv.scale.y})")
            }
        }


        changeTerrainData()

        context(delta, env, camera) {
            offsetShiftingManager.update(character.manager.instance)
            charController.update(delta)
            cameraController.update()
            postProcess {
                skybox.render()
                terrainInstance.render()
                character.render()
            }
            diagnostics.render()
        }
    }

}