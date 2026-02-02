package org.roldy.core.camera

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

class ThirdPersonCamera(
    val camera: Camera
) {

    // Target to follow
    val targetPosition = Vector3()
    private val targetOffset = Vector3(0f, 1.8f, 0f)  // Height offset (eye level)

    // Orbit parameters
    private var yaw = 0f      // Horizontal angle (radians)
    private var pitch = 0.3f  // Vertical angle (radians)
    private var distance = 200f // Distance from target

    // Limits
    var minDistance = 2f
    var maxDistance = 1000f
    var minPitch = -1.2f  // How far down (radians, ~-70°)
    var maxPitch = 1.4f   // How far up (radians, ~80°)

    // Sensitivity
    var rotationSensitivity = 0.005f
    var zoomSensitivity = 1f
    var smoothing = 10f  // Camera lag (higher = snappier)

    // Input state
    private var isDragging = false
    private var lastMouseX = 0
    private var lastMouseY = 0

    // Smooth follow
    private val currentPosition = Vector3()
    private val desiredPosition = Vector3()
    private val lookAtPosition = Vector3()
    private var isMoving = false  // Add this

    fun setMoving(moving: Boolean) {
        isMoving = moving
    }

    // Temp vectors
    private val tmp = Vector3()

    /**
     * Set the target position to follow (usually player position)
     */
    fun setTarget(x: Float, y: Float, z: Float) {
        targetPosition.set(x, y, z)
    }

    fun setTarget(position: ModelInstance) {
        targetPosition.set(position.transform.getTranslation(Vector3()))
    }

    /**
     * Set height offset from target (e.g., character height)
     */
    fun setTargetOffset(x: Float, y: Float, z: Float) {
        targetOffset.set(x, y, z)
    }

    /**
     * Process input - call this in your input processor or update
     */
    fun handleInput() {
        // Right-click drag to rotate (WoW style)
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (!isDragging) {
                isDragging = true
                lastMouseX = Gdx.input.x
                lastMouseY = Gdx.input.y
                Gdx.input.isCursorCatched = true
            }

            val deltaX = Gdx.input.x - lastMouseX
            val deltaY = Gdx.input.y - lastMouseY

            yaw -= deltaX * rotationSensitivity
            pitch += deltaY * rotationSensitivity

            // Clamp pitch
            pitch = MathUtils.clamp(pitch, minPitch, maxPitch)

            lastMouseX = Gdx.input.x
            lastMouseY = Gdx.input.y
        } else {
            if (isDragging) {
                isDragging = false
                Gdx.input.isCursorCatched = false
            }
        }

        // Scroll to zoom
        val scroll = Gdx.input.isKeyPressed(Input.Keys.MINUS).toInt() -
                Gdx.input.isKeyPressed(Input.Keys.EQUALS).toInt()
        if (scroll != 0) {
            distance += scroll * zoomSensitivity * 0.5f
            distance = MathUtils.clamp(distance, minDistance, maxDistance)
        }
    }

    /**
     * Handle scroll wheel (call from InputProcessor.scrolled)
     */
    fun scrolled(amountY: Float) {
        distance += amountY * zoomSensitivity
        distance = MathUtils.clamp(distance, minDistance, maxDistance)
    }

    /**
     * Update camera position - call every frame
     */
    context(deltaTime: Float)
    fun update() {
        // Calculate look-at point (target + offset)
        lookAtPosition.set(targetPosition).add(targetOffset)

        // Calculate desired camera position using spherical coordinates
        val horizontalDist = distance * MathUtils.cos(pitch)
        desiredPosition.set(
            lookAtPosition.x + horizontalDist * MathUtils.sin(yaw),
            lookAtPosition.y + distance * MathUtils.sin(pitch),
            lookAtPosition.z + horizontalDist * MathUtils.cos(yaw)
        )

        // Only smooth when moving, snap when stopped
        if (isMoving) {
            val alpha = MathUtils.clamp(smoothing * deltaTime, 0f, 1f)
            currentPosition.lerp(desiredPosition, alpha)
        } else {
            currentPosition.set(desiredPosition)  // Instant snap
        }

        // Update camera
        camera.position.set(currentPosition)
        camera.lookAt(lookAtPosition)
        camera.up.set(Vector3.Y)
        camera.update()
    }

    /**
     * Get the forward direction (for character movement relative to camera)
     */
    fun getForwardDirection(out: Vector3): Vector3 {
        return out.set(
            MathUtils.sin(yaw),
            0f,
            MathUtils.cos(yaw)
        ).nor()
    }

    /**
     * Get the right direction
     */
    fun getRightDirection(out: Vector3): Vector3 {
        return out.set(
            MathUtils.sin(yaw - MathUtils.HALF_PI),
            0f,
            MathUtils.cos(yaw - MathUtils.HALF_PI)
        ).nor()
    }

    /**
     * Get yaw angle (for rotating character to face camera direction)
     */
    fun getYawDegrees(): Float = yaw * MathUtils.radiansToDegrees

    fun getYawRadians(): Float = yaw

    /**
     * Set camera angle directly
     */
    fun setYaw(radians: Float) {
        yaw = radians
    }

    fun setPitch(radians: Float) {
        pitch = MathUtils.clamp(radians, minPitch, maxPitch)
    }

    fun setDistance(dist: Float) {
        distance = MathUtils.clamp(dist, minDistance, maxDistance)
    }

    /**
     * Resize handler
     */
    fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    private fun Boolean.toInt() = if (this) 1 else 0


}