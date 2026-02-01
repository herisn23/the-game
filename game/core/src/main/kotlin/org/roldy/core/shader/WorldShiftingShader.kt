package org.roldy.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import kotlin.reflect.KProperty

open class WorldShiftingShader(
    private val renderable: Renderable,
    config: Config = Config().apply {
        vertexShader = ShaderLoader.defaultVert
        fragmentShader = ShaderLoader.defaultFrag
    },
    private val offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    },
) : DefaultShader(renderable, config) {
    val u_shiftOffset by Delegate()

    override fun render(renderable: Renderable) {
        shift()
        super.render(renderable)
    }

    override fun end() {
        // Reset texture unit after terrain rendering
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }

    fun Pair<Texture, Int>.bind(unit: Int) {
        first.bind(unit)
        program.setUniformi(second, unit)
    }

    fun shift() {
        val originOffset = offsetProvider.shiftOffset
        program.setUniformf(u_shiftOffset, originOffset.x, originOffset.y, originOffset.z)
    }

    inner class Delegate(
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