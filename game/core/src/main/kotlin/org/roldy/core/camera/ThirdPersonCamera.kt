package org.roldy.core.camera

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3

class ThirdPersonCamera(
    private val camera: PerspectiveCamera,
    private val target: ModelInstance
) : InputAdapter() {
    val zoomSensitivity = 2f
    val rotateSensitivity = 0.3f
    var rightMouseDown = false
    var height = 300f         // Height above target
    var lookAtHeight = 50f   // Look at point (character head)

    var yaw = 90f            // Horizontal rotation (degrees)
    var pitch = 10f         // Vertical angle (degrees)
    var minDistance = 50f
    var maxDistance = 5000f
    var distance = maxDistance    // Distance behind target
    fun zoom(amount: Float) {
        distance = (distance + amount).coerceIn(minDistance, maxDistance)
    }

    private var lastX = 0
    private var lastY = 0
    private val targetPos = Vector3()
    private val cameraOffset = Vector3()
    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        zoom(amountY * zoomSensitivity)
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        lastX = screenX
        lastY = screenY
        when (button) {
            Input.Buttons.RIGHT -> rightMouseDown = true
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        when (button) {
            Input.Buttons.RIGHT -> rightMouseDown = false
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val deltaX = screenX - lastX
        val deltaY = screenY - lastY
        lastX = screenX
        lastY = screenY

        when {
            // Right click = Look around (FPS style)
            rightMouseDown -> rotate(-deltaX * rotateSensitivity, deltaY * rotateSensitivity)
        }

        return true
    }


    context(delta: Float)
    fun update() {
        // Get target position from transform
        target.transform.getTranslation(targetPos)

        // Calculate camera position based on yaw/pitch
        val yawRad = Math.toRadians(yaw.toDouble()).toFloat()
        val pitchRad = Math.toRadians(pitch.toDouble()).toFloat()

        cameraOffset.set(
            -distance * kotlin.math.cos(pitchRad) * kotlin.math.sin(yawRad),
            distance * kotlin.math.sin(pitchRad) + height,
            -distance * kotlin.math.cos(pitchRad) * kotlin.math.cos(yawRad)
        )

        camera.position.set(targetPos).add(cameraOffset)
        camera.lookAt(targetPos.x, targetPos.y + lookAtHeight, targetPos.z)
        camera.up.set(Vector3.Y)
        camera.update()
    }

    fun rotate(deltaX: Float, deltaY: Float) {
        yaw += deltaX
        pitch = (pitch + deltaY).coerceIn(-10f, 80f)  // Clamp vertical
    }
}