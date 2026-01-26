package org.roldy.editor

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import kotlin.math.cos
import kotlin.math.sin

class EditorCameraController(
    private val camera: PerspectiveCamera
) : InputAdapter() {

    // Movement settings
    var moveSpeed = 50f
    var fastMultiplier = 5f
    var panSpeed = 0.5f
    var orbitSpeed = 0.3f
    var zoomSpeed = 5f
    var smoothness = 10f

    // State
    private var lastX = 0
    private var lastY = 0

    private var rightMouseDown = false
    private var middleMouseDown = false
    private var leftMouseDown = false
    private var altDown = false
    private var shiftDown = false
    private var ctrlDown = false

    // Orbit
    private val orbitTarget = Vector3()
    private var orbitDistance = 50f
    private var yaw = 0f
    private var pitch = -30f

    // Smooth movement
    private val velocity = Vector3()
    private val targetPosition = Vector3()
    private val tempVec = Vector3()

    // Keys pressed
    private val keysPressed = mutableSetOf<Int>()

    init {
        targetPosition.set(camera.position)
        updateOrbitTarget()
    }

    override fun keyDown(keycode: Int): Boolean {
        keysPressed.add(keycode)
        when (keycode) {
            Input.Keys.ALT_LEFT, Input.Keys.ALT_RIGHT -> altDown = true
            Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> shiftDown = true
            Input.Keys.CONTROL_LEFT, Input.Keys.CONTROL_RIGHT -> ctrlDown = true
            Input.Keys.F -> focusOnTarget()
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        keysPressed.remove(keycode)
        when (keycode) {
            Input.Keys.ALT_LEFT, Input.Keys.ALT_RIGHT -> altDown = false
            Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> shiftDown = false
            Input.Keys.CONTROL_LEFT, Input.Keys.CONTROL_RIGHT -> ctrlDown = false
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        lastX = screenX
        lastY = screenY

        when (button) {
            Input.Buttons.RIGHT -> rightMouseDown = true
            Input.Buttons.MIDDLE -> middleMouseDown = true
            Input.Buttons.LEFT -> {
                leftMouseDown = true
                if (altDown) {
                    updateOrbitTarget()
                }
            }
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        when (button) {
            Input.Buttons.RIGHT -> rightMouseDown = false
            Input.Buttons.MIDDLE -> middleMouseDown = false
            Input.Buttons.LEFT -> leftMouseDown = false
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val deltaX = screenX - lastX
        val deltaY = screenY - lastY
        lastX = screenX
        lastY = screenY

        when {
            // Alt + Left click = Orbit
            altDown && leftMouseDown -> orbit(deltaX, deltaY)

            // Right click = Look around (FPS style)
            rightMouseDown -> lookAround(deltaX, deltaY)

            // Middle mouse = Pan
            middleMouseDown -> pan(deltaX, deltaY)
        }

        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        if (altDown) {
            // Zoom to orbit target
            orbitDistance = (orbitDistance + amountY * zoomSpeed).coerceIn(1f, 500f)
            updateCameraOrbit()
        } else {
            // Dolly forward/backward
            tempVec.set(camera.direction).scl(-amountY * zoomSpeed)
            targetPosition.add(tempVec)
        }
        return true
    }

    private fun lookAround(deltaX: Int, deltaY: Int) {
        yaw -= deltaX * orbitSpeed
        pitch -= deltaY * orbitSpeed
        pitch = pitch.coerceIn(-89f, 89f)

        updateCameraDirection()
    }

    private fun orbit(deltaX: Int, deltaY: Int) {
        yaw -= deltaX * orbitSpeed
        pitch -= deltaY * orbitSpeed
        pitch = pitch.coerceIn(-89f, 89f)

        updateCameraOrbit()
    }

    private fun pan(deltaX: Int, deltaY: Int) {
        // Pan relative to camera orientation
        tempVec.set(camera.direction).crs(camera.up).nor().scl(-deltaX * panSpeed * 0.1f)
        targetPosition.add(tempVec)
        orbitTarget.add(tempVec)

        tempVec.set(camera.up).scl(deltaY * panSpeed * 0.1f)
        targetPosition.add(tempVec)
        orbitTarget.add(tempVec)
    }

    private fun updateCameraDirection() {
        val radYaw = Math.toRadians(yaw.toDouble()).toFloat()
        val radPitch = Math.toRadians(pitch.toDouble()).toFloat()

        camera.direction.set(
            cos(radPitch) * sin(radYaw),
            sin(radPitch),
            cos(radPitch) * cos(radYaw)
        ).nor()

        camera.up.set(Vector3.Y)
    }

    private fun updateCameraOrbit() {
        val radYaw = Math.toRadians(yaw.toDouble()).toFloat()
        val radPitch = Math.toRadians(pitch.toDouble()).toFloat()

        // Position camera around orbit target
        targetPosition.set(
            orbitTarget.x - cos(radPitch) * sin(radYaw) * orbitDistance,
            orbitTarget.y - sin(radPitch) * orbitDistance,
            orbitTarget.z - cos(radPitch) * cos(radYaw) * orbitDistance
        )

        camera.direction.set(orbitTarget).sub(camera.position).nor()
        camera.up.set(Vector3.Y)
    }

    private fun updateOrbitTarget() {
        // Set orbit target in front of camera
        orbitTarget.set(camera.direction).scl(orbitDistance).add(camera.position)
    }

    fun focusOnTarget(target: Vector3 = orbitTarget, distance: Float = 30f) {
        orbitTarget.set(target)
        orbitDistance = distance
        updateCameraOrbit()
    }

    fun update(deltaTime: Float) {
        // WASD movement when right mouse is held
        if (rightMouseDown) {
            val speed = moveSpeed * (if (shiftDown) fastMultiplier else 1f) * deltaTime

            velocity.setZero()

            if (Input.Keys.W in keysPressed || Input.Keys.UP in keysPressed) {
                tempVec.set(camera.direction).scl(speed)
                velocity.add(tempVec)
            }
            if (Input.Keys.S in keysPressed || Input.Keys.DOWN in keysPressed) {
                tempVec.set(camera.direction).scl(-speed)
                velocity.add(tempVec)
            }
            if (Input.Keys.A in keysPressed || Input.Keys.LEFT in keysPressed) {
                tempVec.set(camera.direction).crs(camera.up).nor().scl(-speed)
                velocity.add(tempVec)
            }
            if (Input.Keys.D in keysPressed || Input.Keys.RIGHT in keysPressed) {
                tempVec.set(camera.direction).crs(camera.up).nor().scl(speed)
                velocity.add(tempVec)
            }
            if (Input.Keys.Q in keysPressed) {
                tempVec.set(0f, -speed, 0f)
                velocity.add(tempVec)
            }
            if (Input.Keys.E in keysPressed) {
                tempVec.set(0f, speed, 0f)
                velocity.add(tempVec)
            }

            targetPosition.add(velocity)
            orbitTarget.add(velocity)
        }

        // Smooth camera position
        camera.position.lerp(targetPosition, smoothness * deltaTime)
        camera.update()
    }
}