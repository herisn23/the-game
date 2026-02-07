package org.roldy.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.shader.WorldShiftingShader.CreatePrefix.create
import kotlin.reflect.KProperty

open class WorldShiftingShader(
    renderable: Renderable,
    config: Config = Config().apply {
        vertexShader = ShaderLoader.defaultVert
        fragmentShader = ShaderLoader.defaultFrag
    },
    val offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    },
) : DefaultShader(renderable, config.apply {
    vertexShader = vertexShader?.create()
    fragmentShader = fragmentShader?.create()
}) {
    private object CreatePrefix {
        fun String.create() = run {
            """
                #define shiftFlag
                $this
            """.trimIndent()
        }
    }

    val u_shiftOffset by FetchUniform()

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
        // Reset texture unit after terrain rendering
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }

    fun shift() {
        val originOffset = offsetProvider.shiftOffset
        program.setUniformf(u_shiftOffset, originOffset.x, originOffset.y, originOffset.z)
    }

    inner class FetchUniform(
        private val normalize: Boolean = false
    ) {
        private var cachedLocation: Int? = null

        operator fun getValue(thisRef: WorldShiftingShader, property: KProperty<*>): Int {
            return cachedLocation ?: program.fetchUniformLocation(
                property.name,
                normalize
            ).also { cachedLocation = it }
        }
    }
}


fun shiftingShaderProvider(offsetProvider: OffsetProvider) =
    shaderProvider {
        WorldShiftingShader(it, offsetProvider = offsetProvider)
    }