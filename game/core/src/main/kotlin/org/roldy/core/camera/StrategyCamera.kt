package org.roldy.core.camera

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

class StrategyCameraController(val camera: PerspectiveCamera) : InputAdapter() {

    // Movement settings
    var panSpeed = 20f
    var zoomSpeed = 20f
    var rotationSpeed = 90f
    var edgeScrollSpeed = 15f
    var edgeScrollMargin = 20 // Pixels from screen edge
    var smoothness = 5f

    // Zoom limits
    var minDistance = 2000f
    var maxDistance = minDistance + 100000f

    // Angle limits
    var minAngle = 20f
    var maxAngle = 80f

    // Boundary limits
    var minX = -50f
    var maxX = 50f
    var minZ = -50f
    var maxZ = 50f

    // Camera position and target
    private val target = Vector3()
    private var distance = minDistance
    private var angle = 45f // Pitch angle in degrees
    private var rotation = 0f // Yaw rotation around target

    // Input state
    private var middleButtonPressed = false
    private var rightButtonPressed = false
    private var lastMouseX = 0
    private var lastMouseY = 0

    // Smooth movement targets
    private val targetPosition = Vector3()
    private var targetDistance = distance
    private var targetAngle = angle
    private var targetRotation = rotation

    // Temp vector
    private val tmpVec = Vector3()

    init {
        targetPosition.set(target)
        updateCameraPosition()
    }

    context(deltaTime: Float)
    fun update() {
        handleKeyboardInput(deltaTime)
        handleEdgeScrolling(deltaTime)

        // Smooth interpolation
        target.lerp(targetPosition, smoothness * deltaTime)
        distance = MathUtils.lerp(distance, targetDistance, smoothness * deltaTime)
        angle = MathUtils.lerp(angle, targetAngle, smoothness * deltaTime)
        rotation = MathUtils.lerp(rotation, targetRotation, smoothness * deltaTime)

        updateCameraPosition()
    }

    private fun handleKeyboardInput(deltaTime: Float) {
        val moveAmount = panSpeed * deltaTime

        // Calculate forward and right vectors based on camera rotation
        val radians = rotation * MathUtils.degreesToRadians
        val forwardX = MathUtils.sin(radians)
        val forwardZ = MathUtils.cos(radians)
        val rightX = MathUtils.cos(radians)
        val rightZ = -MathUtils.sin(radians)

        // WASD or Arrow keys for panning
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            targetPosition.x -= forwardX * moveAmount
            targetPosition.z -= forwardZ * moveAmount
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            targetPosition.x += forwardX * moveAmount
            targetPosition.z += forwardZ * moveAmount
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            targetPosition.x -= rightX * moveAmount
            targetPosition.z -= rightZ * moveAmount
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            targetPosition.x += rightX * moveAmount
            targetPosition.z += rightZ * moveAmount
        }

        // Q/E for rotation
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            targetRotation -= rotationSpeed * deltaTime
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            targetRotation += rotationSpeed * deltaTime
        }

        // R/F for zoom
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            targetDistance -= zoomSpeed * deltaTime * 5f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            targetDistance += zoomSpeed * deltaTime * 5f
        }

        clampValues()
    }

    private fun handleEdgeScrolling(deltaTime: Float) {
        val mouseX = Gdx.input.x
        val mouseY = Gdx.input.y
        val screenWidth = Gdx.graphics.width
        val screenHeight = Gdx.graphics.height

        val moveAmount = edgeScrollSpeed * deltaTime

        val radians = rotation * MathUtils.degreesToRadians
        val forwardX = MathUtils.sin(radians)
        val forwardZ = MathUtils.cos(radians)
        val rightX = MathUtils.cos(radians)
        val rightZ = -MathUtils.sin(radians)

        // Horizontal edge scrolling
        when {
            mouseX < edgeScrollMargin -> {
                targetPosition.x -= rightX * moveAmount
                targetPosition.z -= rightZ * moveAmount
            }

            mouseX > screenWidth - edgeScrollMargin -> {
                targetPosition.x += rightX * moveAmount
                targetPosition.z += rightZ * moveAmount
            }
        }

        // Vertical edge scrolling
        when {
            mouseY < edgeScrollMargin -> {
                targetPosition.x -= forwardX * moveAmount
                targetPosition.z -= forwardZ * moveAmount
            }

            mouseY > screenHeight - edgeScrollMargin -> {
                targetPosition.x += forwardX * moveAmount
                targetPosition.z += forwardZ * moveAmount
            }
        }

        clampValues()
    }

    private fun updateCameraPosition() {
        val radAngle = angle * MathUtils.degreesToRadians
        val radRotation = rotation * MathUtils.degreesToRadians

        // Calculate camera position based on spherical coordinates
        val horizontalDistance = distance * MathUtils.cos(radAngle)
        val verticalDistance = distance * MathUtils.sin(radAngle)

        camera.position.set(
            target.x + horizontalDistance * MathUtils.sin(radRotation),
            target.y + verticalDistance,
            target.z + horizontalDistance * MathUtils.cos(radRotation)
        )

        camera.lookAt(target)
        camera.up.set(Vector3.Y)
        camera.update()
    }

    private fun clampValues() {
        targetDistance = MathUtils.clamp(targetDistance, minDistance, maxDistance)
        targetAngle = MathUtils.clamp(targetAngle, minAngle, maxAngle)
        println("=========x===")
        println("${targetPosition.x}")
        targetPosition.x = MathUtils.clamp(targetPosition.x, minX, maxX)
        targetPosition.z = MathUtils.clamp(targetPosition.z, minZ, maxZ)
        println("${targetPosition.x}")
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        lastMouseX = screenX
        lastMouseY = screenY

        return when (button) {
            Input.Buttons.MIDDLE -> {
                middleButtonPressed = true
                true
            }

            Input.Buttons.RIGHT -> {
                rightButtonPressed = true
                true
            }

            else -> false
        }
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return when (button) {
            Input.Buttons.MIDDLE -> {
                middleButtonPressed = false
                true
            }

            Input.Buttons.RIGHT -> {
                rightButtonPressed = false
                true
            }

            else -> false
        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val deltaX = screenX - lastMouseX
        val deltaY = screenY - lastMouseY
        lastMouseX = screenX
        lastMouseY = screenY

        // Middle mouse button: rotate camera
        if (middleButtonPressed) {
            targetRotation += deltaX * 0.5f
            targetAngle += deltaY * 0.3f
            clampValues()
            return true
        }

        // Right mouse button: pan camera
        if (rightButtonPressed) {
            val radians = rotation * MathUtils.degreesToRadians
            val rightX = MathUtils.cos(radians)
            val rightZ = -MathUtils.sin(radians)
            val forwardX = MathUtils.sin(radians)
            val forwardZ = MathUtils.cos(radians)

            val panFactor = distance * 0.2f
            targetPosition.x += (rightX * deltaX + forwardX * deltaY) * panFactor
            targetPosition.z += (rightZ * deltaX + forwardZ * deltaY) * panFactor
            clampValues()
            return true
        }

        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        targetDistance += amountY * zoomSpeed
        clampValues()
        return true
    }

    // Utility functions
    fun setBounds(minX: Float, maxX: Float, minZ: Float, maxZ: Float) {
        this.minX = minX
        this.maxX = maxX
        this.minZ = minZ
        this.maxZ = maxZ
    }

    fun setZoomLimits(min: Float, max: Float) {
        minDistance = min
        maxDistance = max
    }

    fun setAngleLimits(min: Float, max: Float) {
        minAngle = min
        maxAngle = max
    }

    fun setTarget(x: Float, y: Float, z: Float) {
        targetPosition.set(x, y, z)
    }

    fun getTarget(): Vector3 = target

    fun getDistance(): Float = distance

    // Jump to position immediately (no smoothing)
    fun jumpTo(x: Float, z: Float) {
        targetPosition.set(x, target.y, z)
        target.set(targetPosition)
    }

    fun jumpToDistance(dist: Float) {
        targetDistance = MathUtils.clamp(dist, minDistance, maxDistance)
        distance = targetDistance
    }

    fun jumpToRotation(rot: Float) {
        targetRotation = rot
        rotation = rot
    }
}