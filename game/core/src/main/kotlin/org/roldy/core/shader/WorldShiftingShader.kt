package org.roldy.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.shader.util.ShaderBuilder
import org.roldy.core.shader.util.ShaderUserData
import org.roldy.core.shader.util.fetchUniform
import org.roldy.core.shader.util.shaderProvider

open class WorldShiftingShader(
    renderable: Renderable,
    config: Config = Config().apply {
        vertexShader = ShaderLoader.defaultVert
        fragmentShader = ShaderLoader.defaultFrag
    },
    val offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    }
) : DefaultShader(renderable, config.apply {
    with(ShaderBuilder) {
        vertexShader = vertexShader?.shiftFlag()
    }
}) {

    val defaultOffset = Vector3()

    val u_shiftOffset by program.fetchUniform()

    val worldShiftUserData = renderable.userData as? ShaderUserData
    val isShifted = worldShiftUserData?.shifted ?: false

    inner class TextureBind(
        val texture: Texture,
        val uniform: Int,
        val bind: Int
    ) {
        fun bind() {
            texture.bind(bind)
            program.setUniformi(uniform, bind)
        }
    }

    fun Texture.prepare(uniform: Int, bind: Int) =
        TextureBind(this, uniform, bind)

    override fun render(renderable: Renderable) {
        shift()
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

}


fun shiftingShaderProvider(offsetProvider: OffsetProvider) =
    shaderProvider {
        WorldShiftingShader(it, offsetProvider = offsetProvider)
    }


