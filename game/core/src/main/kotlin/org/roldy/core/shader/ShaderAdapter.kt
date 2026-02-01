package org.roldy.core.shader

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.math.Vector3
import org.roldy.core.camera.OffsetProvider
import java.util.Locale.getDefault
import kotlin.reflect.KProperty

abstract class ShaderAdapter(
    private val renderable: Renderable,
    config: Config,
    private val offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    },
) : DefaultShader(renderable, config) {
    val uDiffuseColor by Delegate()
    val uDiffuseTexture by Delegate()
    val uEmissiveColor by Delegate()
    val uEmissiveTexture by Delegate()
    val uLightDir by Delegate()
    val uLightColor by Delegate()
    val uAmbientLight by Delegate()
    val uRenderOffset by Delegate()

    val ambientLight by lazy { renderable.environment.get(ColorAttribute::class.java, ColorAttribute.AmbientLight) }
    val light by lazy {
        renderable.environment.run {
            get(
                DirectionalLightsAttribute::class.java,
                DirectionalLightsAttribute.Type
            ) as DirectionalLightsAttribute
        }.lights.first()
    }
    val diffuseTexture by lazy {
        renderable.material.get(
            TextureAttribute::
            class.java, TextureAttribute.Diffuse
        ).textureDescription.texture to uDiffuseTexture
    }


    val emissiveTexture by lazy {
        renderable.material.get(
            TextureAttribute::
            class.java, TextureAttribute.Emissive
        ).textureDescription.texture to uEmissiveTexture
    }

    fun Pair<Texture, Int>.bind(unit: Int) {
        first.bind(unit)
        program.setUniformi(second, unit)
    }

    fun setAmbientLightColor() {
        program.setUniformf(
            uAmbientLight,
            ambientLight.color.r,
            ambientLight.color.g,
            ambientLight.color.b
        )
    }

    fun setLightColor() {
        program.setUniformf(
            uLightColor,
            light.color.r,
            light.color.g,
            light.color.b
        )
    }

    fun setLightDir() {
        program.setUniformf(
            uLightDir,
            light.direction.x,
            light.direction.y,
            light.direction.z
        )
    }

    fun shift() {
        val originOffset = offsetProvider.shiftOffset
        program.setUniformf(uRenderOffset, originOffset.x, originOffset.y, originOffset.z)
    }

    inner class Delegate(
        private val normalize: Boolean = false
    ) {
        private var cachedLocation: Int? = null

        operator fun getValue(thisRef: ShaderAdapter, property: KProperty<*>): Int {
            return cachedLocation ?: program.fetchUniformLocation(
                property.name.normalize(),
                normalize
            ).also { cachedLocation = it }
        }

        private fun String.normalize() =
            replaceFirst("u", "")
                .replaceFirstChar { it.lowercase(getDefault()) }
                .run { "u_$this" }
    }
}