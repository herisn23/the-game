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
import org.roldy.core.shader.util.ShaderBuilder
import org.roldy.core.shader.util.ShaderUserData
import org.roldy.core.shader.util.WindShaderManager
import org.roldy.core.shader.util.shaderProvider
import org.roldy.core.system.WindSystem
import kotlin.reflect.KProperty

class ShiftedDepthShader(
    renderable: Renderable,
    private val windSystem: WindSystem,
    val offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    }
) : DepthShader(
    renderable, Config().apply {
        with(ShaderBuilder) {
            vertexShader = ShaderLoader.depthVert.append(ShaderLoader.windSystem).shiftFlag().windFlag(renderable)
            fragmentShader = ShaderLoader.depthFrag
        }
    }
) {
    val defaultOffset = Vector3()
    val windManager = WindShaderManager(windSystem, program)

    val u_shiftOffset by FetchUniform()

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
        windManager.render(renderable)
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


fun shiftingDepthShaderProvider(offsetProvider: OffsetProvider, windSystem: WindSystem) =
    shaderProvider {
        ShiftedDepthShader(it, windSystem, offsetProvider = offsetProvider)
    }