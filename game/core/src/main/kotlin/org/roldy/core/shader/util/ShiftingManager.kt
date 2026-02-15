package org.roldy.core.shader.util

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import org.roldy.core.camera.OffsetProvider

class ShiftingManager(
    val program: ShaderProgram,
    val offsetProvider: OffsetProvider
) {
    val defaultOffset = Vector3()

    val u_shiftOffset by program.fetchUniform()

    fun shift(renderable: Renderable) {
        val worldShiftUserData = renderable.userData as? ShaderUserData
        val isShifted = worldShiftUserData?.shifted ?: false
        val originOffset = offsetProvider.shiftOffset.takeIf { isShifted } ?: defaultOffset
        program.setUniformf(u_shiftOffset, originOffset.x, originOffset.y, originOffset.z)
    }
}