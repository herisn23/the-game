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
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.postprocess.PostProcessing
import org.roldy.core.utils.hex
import org.roldy.core.utils.sequencer
import org.roldy.g3d.pawn.*


class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {
    var loading = true
    val postProcess = PostProcessing(camera)

    init {
        camera.position.set(16.799997f, 0f, 0f)
        camera.lookAt(0f, 0f, 0f)
        camera.update()
    }

    val character2 by disposable {
        PawnManager(BodyType.Male).apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(-10f, -9.700001f, 10f)
            initialTransform.rotate(Vector3.Y, 95f)
            instance.transform.set(initialTransform) // Copy it over
        }.run {
            PawnRenderer(this, camera)
        }
    }
    val character3 by disposable {
        PawnManager(BodyType.Male).apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(-30f, -9.700001f, -10f)
            initialTransform.rotate(Vector3.Y, 80f)
            instance.transform.set(initialTransform) // Copy it over
        }.run {
            PawnRenderer(this, camera)
        }
    }
    val character by disposable {
        PawnManager().apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(0f, -9.700001f, 0f)
            initialTransform.rotate(Vector3.Y, 90f)
            instance.transform.set(initialTransform) // Copy it over
        }.run {
            PawnRenderer(this, camera)
        }
    }

    val light = DirectionalLight().set(hex("FFF4D6"), -1f, 1f, -0.2f)
    val env by lazy {
        Environment().apply {
            set(ColorAttribute(ColorAttribute.Emissive, hex("FFF4D6")))
            add(light)
        }
    }

    fun printNodeHierarchy(node: Node, indent: String = "") {
        println("$indent${node.id}")
        for (child in node.children) {
            printNodeHierarchy(child, "$indent  ")
        }
    }

    val controller by lazy {
        ModelController(character.manager.instance, camera).also(Gdx.input::setInputProcessor).apply {
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

        }
        if (loading) return
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            character.manager.cycleSets()
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            light.direction.x += delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            light.direction.x -= delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            light.direction.y += delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            light.direction.y -= delta
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            postProcess.toggle()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            character.manager.animationController.setAnimation(anims.next().id, -1)
        }
        camera.update()
        controller.update()

        postProcess {
            context(delta, env) {
                character2.render()
                character3.render()
                character.render()
            }
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
        }
        model.transform.setTranslation(position)
    }

}