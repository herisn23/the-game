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
import org.roldy.core.shader.util.ShiftingManager
import org.roldy.core.shader.util.shaderProvider

open class ShiftingShader(
    renderable: Renderable,
    config: Config = Config().apply {
        vertexShader = ShaderLoader.defaultVert
        fragmentShader = ShaderLoader.defaultFrag
    },
    offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    }
) : DefaultShader(renderable, config.apply {
    with(ShaderBuilder) {
        vertexShader = vertexShader?.shiftFlag()
    }
}) {

    private val shiftingManager = ShiftingManager(program, offsetProvider)


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
        shiftingManager.shift(renderable)
        super.render(renderable)
    }

    override fun end() {
        super.end()
        // Reset texture unit after terrain rendering
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }



}


fun shiftingShaderProvider(offsetProvider: OffsetProvider) =
    shaderProvider {
        ShiftingShader(it, offsetProvider = offsetProvider)
    }


