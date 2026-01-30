package org.roldy.g3d.pawn

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import org.roldy.g3d.terrain.TerrainRaycaster

class RTSInputHandler(
    private val camera: Camera,
    private val raycaster: TerrainRaycaster,
    private val characterController: RTSCharacterController
) : InputAdapter() {

    // Optional: visual marker for click position
    var onTargetSet: ((Vector3) -> Unit)? = null

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {  // Right-click to move
            val hitPoint = raycaster.pickTerrain(screenX.toFloat(), screenY.toFloat())

            if (hitPoint != null) {
                characterController.setTarget(hitPoint)
                onTargetSet?.invoke(hitPoint)
                return true
            }
        }

        if (button == Input.Buttons.LEFT) {
            // Could be used for selection
        }

        return false
    }
}