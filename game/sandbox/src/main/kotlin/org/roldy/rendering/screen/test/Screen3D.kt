package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import org.roldy.core.Diagnostics
import org.roldy.core.InputProcessorDelegate
import org.roldy.core.coroutines.async
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.map.MapData
import org.roldy.core.map.MapGenerator
import org.roldy.core.map.MapSize
import org.roldy.core.postprocess.PostProcessing
import org.roldy.core.utils.sequencer
import org.roldy.editor.EditorCameraController
import org.roldy.g3d.pawn.*
import org.roldy.g3d.skybox.Skybox
import org.roldy.g3d.terrain.Terrain
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {
    var loading = true
    val postProcess = PostProcessing()
    val diagnostics by disposable { Diagnostics() }
    init {
        camera.position.set(0f, 20f, 0f)
        camera.update()
        Diagnostics.addProvider { "Chunks: ${terrainInstance.getVisibleCount(camera)} / ${terrainInstance.getTotalCount()}" }
    }

    val mapSize = MapSize(1000, 1000)
    val mapData = MapData(1, mapSize)
    var noiseData = MapGenerator(mapData).generate().apply {
        val elevations = values.map { it.elevation }
        println("Elevation: min=${elevations.minOrNull()}, max=${elevations.maxOrNull()}")
    }
    var terrainInstance = changeTerrain()

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
        return Terrain(noiseData, mapSize)
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
            noiseData = MapGenerator(
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

    val character2 by disposable {
        PawnManager(modelBuilder, BodyType.Male).apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(-10f, -9.700001f, 10f)
            initialTransform.rotate(Vector3.Y, 95f)
            instance.transform.set(initialTransform) // Copy it over
        }.run {
            PawnRenderer(this)
        }
    }
    val character3 by disposable {
        PawnManager(modelBuilder, BodyType.Male).apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(-30f, -9.700001f, -10f)
            initialTransform.rotate(Vector3.Y, 80f)
            instance.transform.set(initialTransform) // Copy it over
        }.run {
            PawnRenderer(this)
        }
    }
    val character by disposable {
        PawnManager(modelBuilder).apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(0f, 0f, 0f)
            initialTransform.rotate(Vector3.Y, 90f)
            instance.transform.set(initialTransform) // Copy it over
        }.run {
            PawnRenderer(this)
        }
    }

    val cameraController = EditorCameraController(camera)

    //    val controller by lazy { ModelController(character.manager.instance, camera) }
    val adapter by lazy {
        InputProcessorDelegate(listOf(cameraController))
            .also(Gdx.input::setInputProcessor)
    }
    val light = DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f)
    val env by lazy {
        Environment().apply {
            set(ColorAttribute.createAmbient(0.4f, 0.4f, 0.4f, 1f))
            add(light)
        }
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
        changeTerrainData()
        camera.update()
//        controller.update()
        cameraController.update(delta)
        context(delta, env, camera) {
            postProcess {
                skybox.render()
                terrainInstance.render()
//                character2.render()
//                character3.render()
                character.render()
            }
//            Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            diagnostics.render()
        }
    }

}

class ModelController(
    val model: ModelInstance,
    val camera: PerspectiveCamera
) : InputAdapter() {
    var position: Vector3 = model.transform.getTranslation(Vector3())
    private var zoom = 10f // Distance from target
    var lastKey = -1
    override fun keyDown(keycode: Int): Boolean {
        lastKey = keycode
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        lastKey = -1
        return true
    }


    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // Zoom in/out
        zoom += amountY * 2f // Adjust multiplier for zoom speed
        zoom = zoom.coerceIn(2f, 500f) // Clamp between min and max distance
        updateCameraPosition()
        return true
    }

    private fun updateCameraPosition() {
        // Move camera along its current direction
        val direction = Vector3(camera.direction).nor()
        camera.position.set(0f, 0f, 0f).add(direction.scl(-zoom))
        camera.lookAt(0f, 0f, 0f)
        camera.update()

    }

    val rotateSpeed = 2f
    private var yaw = -90f // Left/right
    private var pitch = 0f // Up/down
    fun update() {
        when (lastKey) {
            Input.Keys.W -> {
                position.y += 0.1f
            }

            Input.Keys.S -> {
                position.y -= 0.1f
            }

            Input.Keys.Q -> {
                model.transform.rotate(Vector3.Y, 1f)
            }

            Input.Keys.E -> {
                model.transform.rotate(Vector3.Y, -1f)
            }

            (Input.Keys.L) -> {
                yaw -= rotateSpeed
            }

            (Input.Keys.J) -> yaw += rotateSpeed
            (Input.Keys.K) -> pitch -= rotateSpeed
            (Input.Keys.I) -> pitch += rotateSpeed
        }
        model.transform.setTranslation(position)

        // Clamp pitch to avoid flipping
        pitch = pitch.coerceIn(-89f, 89f)

        // Calculate direction from angles
        val radYaw = Math.toRadians(yaw.toDouble()).toFloat()
        val radPitch = Math.toRadians(pitch.toDouble()).toFloat()

        camera.direction.set(
            cos(radPitch) * sin(radYaw),
            sin(radPitch),
            cos(radPitch) * cos(radYaw)
        )
        camera.up.set(0f, 1f, 0f)
        camera.update()
    }

}