package org.roldy.core.shader.util

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Quaternion
import org.roldy.core.system.WindSystem

class WindShaderManager(
    private val windSystem: WindSystem,
    private val program: ShaderProgram
) {
    val enabled = 1
    private val tempQuat = Quaternion()

    // controller uniforms
    val u_useGlobalWeatherController by program.fetchUniform()
    val u_windDirection by program.fetchUniform()
    val u_galeStrength by program.fetchUniform()
    val u_windIntensity by program.fetchUniform()

    val u_time by program.fetchUniform()

    // configuring uniforms
    val u_objectYRotationDeg by program.fetchUniform()
    val u_useVertexColorWind by program.fetchUniform()

    // Gale bend
    val u_galeBend by program.fetchUniform()

    // Strong wind
    val u_strongWindFrequency by program.fetchUniform()
    val u_strongWindStrength by program.fetchUniform()
    val u_enableStrongWind by program.fetchUniform()

    // Wind twist
    val u_windTwistStrength by program.fetchUniform()
    val u_enableWindTwist by program.fetchUniform()

    // Light wind
    val u_lightWindYOffset by program.fetchUniform()
    val u_lightWindYStrength by program.fetchUniform()
    val u_lightWindStrength by program.fetchUniform()
    val u_enableLightWind by program.fetchUniform()
    val u_lightWindUseLeafFade by program.fetchUniform()

    // Breeze
    val u_breezeStrength by program.fetchUniform()
    val u_enableBreeze by program.fetchUniform()


    fun render(renderable: Renderable) {
        val yaw = renderable.worldTransform.getRotation(tempQuat).yaw

        program.setUniformf(u_time, windSystem.time)

        program.setUniformf(u_objectYRotationDeg, yaw)
        program.setUniformi(u_useVertexColorWind, 1)

        // enablers

        program.setUniformi(u_enableBreeze, enabled)
        program.setUniformi(u_enableLightWind, enabled)
        program.setUniformi(u_enableStrongWind, enabled)
        program.setUniformi(u_enableWindTwist, enabled)

        //breeze
        program.setUniformf(u_breezeStrength, .2f)

        //light wind
        program.setUniformf(u_lightWindStrength, .5f)
        program.setUniformf(u_lightWindYStrength, 1f)
        program.setUniformf(u_lightWindYOffset, 0f)
        program.setUniformi(u_lightWindUseLeafFade, 1)

        // strong wind
        program.setUniformf(u_strongWindStrength, 0.5f)
        program.setUniformf(u_strongWindFrequency, 0.5f)

        // twist
        program.setUniformf(u_windTwistStrength, 0.15f)
        program.setUniformf(u_galeBend, 1f)

    }

}