package org.roldy.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.system.WindAttributes
import kotlin.reflect.KProperty

class ShiftedDepthShader(
    renderable: Renderable,
    private val windAttributes: WindAttributes,
    val offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    }
) : DepthShader(
    renderable, Config().apply {
        with(ShaderBuilder) {
            vertexShader = ShaderLoader.depthVert.shiftFlag().windFlag(renderable)
            fragmentShader = ShaderLoader.depthFrag
        }
    }
) {
    val defaultOffset = Vector3()

    val u_shiftOffset by FetchUniform()

    // Wind uniforms
    val u_time by FetchUniform()
    val u_windStrength by FetchUniform()
    val u_windSpeed by FetchUniform()
    val u_windDirection by FetchUniform()

    val worldShiftUserData = renderable.userData as? ShaderUserData
    val isShifted get() = worldShiftUserData?.shifted ?: false

    override fun begin(camera: Camera, context: RenderContext) {
        super.begin(camera, context)
        // Enable culling for shadow pass
        context.setCullFace(GL20.GL_FRONT)
    }

    override fun render(renderable: Renderable) {
        shift()

        // Set wind uniforms
        program.setUniformf(u_time, windAttributes.time)
        program.setUniformf(u_windStrength, windAttributes.windStrength)
        program.setUniformf(u_windSpeed, windAttributes.windSpeed)
        program.setUniformf(u_windDirection, windAttributes.windDirection.x, windAttributes.windDirection.y)

        super.render(renderable)
    }

    override fun end() {
        super.end()
        // Reset texture unit after terrain rendering
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }

    fun shift() {
        val originOffset = offsetProvider.shiftOffset.takeIf { isShifted } ?: defaultOffset
        program.setUniformf(u_shiftOffset, originOffset.x, originOffset.y, originOffset.z)
    }

    inner class FetchUniform(
        private val normalize: Boolean = false
    ) {
        private var cachedLocation: Int? = null

        operator fun getValue(thisRef: ShiftedDepthShader, property: KProperty<*>): Int {
            return cachedLocation ?: program.fetchUniformLocation(
                property.name,
                normalize
            ).also { cachedLocation = it }
        }
    }
}


fun shiftingDepthShaderProvider(offsetProvider: OffsetProvider, windAttributes: WindAttributes) =
    shaderProvider {
        ShiftedDepthShader(it, windAttributes, offsetProvider = offsetProvider)
    }