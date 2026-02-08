package org.roldy.core.camera

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import org.roldy.core.HeightSampler

class SimpleThirdPersonCamera(
    val camera: Camera,
    val sampler: HeightSampler
) : InputAdapter() {
    var onZoomChanged: (Float) -> Unit = {}
    private val offset = Vector3(0f, 1f, 0f)
    var minCameraHeight = 0f  // Minimum height above terrain
    var yaw = 0f
    var pitch = 0.4f
    var distance = 3f
    var minDistance = 1f // 150
    var maxDistance = 20f // 600

    // Input state
    private var wPressed = false
    private var aPressed = false
    private var sPressed = false
    private var dPressed = false
    private var rightMousePressed = false
    private var lastX = 0
    private var lastY = 0

    // Temp vectors
    private val forward = Vector3()
    private val right = Vector3()
    private val velocity = Vector3()

    // Character rotation
    var characterRotation = 0f
        private set
    private var targetRotation = 0f
    var rotationSpeed = 10f

    // Track if moving this frame (for external use)
    var isMoving = false
        private set

    fun update(playerPosition: Vector3, delta: Float, moveSpeed: Float) {
        velocity.setZero()
        isMoving = false

        if (wPressed || aPressed || sPressed || dPressed) {
            isMoving = true
            forward.set(-MathUtils.sin(yaw), 0f, -MathUtils.cos(yaw))
            right.set(-MathUtils.sin(yaw - MathUtils.HALF_PI), 0f, -MathUtils.cos(yaw - MathUtils.HALF_PI))

            if (wPressed) velocity.add(forward)
            if (sPressed) velocity.sub(forward)
            if (aPressed) velocity.sub(right)
            if (dPressed) velocity.add(right)

            velocity.nor()

            targetRotation = MathUtils.atan2(velocity.x, velocity.z) * MathUtils.radiansToDegrees

            velocity.scl(moveSpeed * delta)
            playerPosition.add(velocity)
        }

        characterRotation = lerpAngle(characterRotation, targetRotation, rotationSpeed * delta)

        updateCameraPosition(playerPosition)
    }

    /**
     * Update camera position directly - call this after origin shift
     */
    fun updateCameraPosition(playerPosition: Vector3) {
        val lookAt = Vector3(playerPosition).add(offset)
        val horizontalDist = distance * MathUtils.cos(pitch)

        val camX = lookAt.x + horizontalDist * MathUtils.sin(yaw)
        var camY = lookAt.y + distance * MathUtils.sin(pitch)
        val camZ = lookAt.z + horizontalDist * MathUtils.cos(yaw)

        // Clamp camera Y to terrain height
        if (sampler.isInBounds(camX, camZ)) {
            val terrainHeight = sampler.getHeightAt(camX, camZ)
            val minY = terrainHeight + minCameraHeight

            if (camY < minY) {
                camY = minY
            }
        }
        camera.position.set(camX, camY, camZ)
        camera.lookAt(lookAt)
        camera.up.set(Vector3.Y)
        camera.update()
    }

    private fun lerpAngle(from: Float, to: Float, t: Float): Float {
        var diff = (to - from + 180) % 360 - 180
        if (diff < -180) diff += 360
        return from + diff * MathUtils.clamp(t, 0f, 1f)
    }

    // InputAdapter overrides - same as before
    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> wPressed = true
            Input.Keys.A -> aPressed = true
            Input.Keys.S -> sPressed = true
            Input.Keys.D -> dPressed = true
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> wPressed = false
            Input.Keys.A -> aPressed = false
            Input.Keys.S -> sPressed = false
            Input.Keys.D -> dPressed = false
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.RIGHT) {
            rightMousePressed = true
            lastX = screenX
            lastY = screenY
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.RIGHT) {
            rightMousePressed = false
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (rightMousePressed) {
            val deltaX = screenX - lastX
            val deltaY = screenY - lastY

            yaw -= deltaX * 0.005f
            pitch += deltaY * 0.005f
            pitch = pitch.coerceIn(-1.2f, 1.4f)

            lastX = screenX
            lastY = screenY
        }
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        distance += amountY * 0.01f
        distance = distance.coerceIn(minDistance, maxDistance)
        onZoomChanged(distance / maxDistance)
        return true
    }
}