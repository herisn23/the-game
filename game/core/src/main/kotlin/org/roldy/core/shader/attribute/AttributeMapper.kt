package org.roldy.core.shader.attribute

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.configuration.data.ColorType
import org.roldy.core.configuration.data.FloatUniform
import org.roldy.core.configuration.data.TexEnvType
import org.roldy.core.configuration.data.Uniform

object AttributeMapper {
    object Generic {
        fun map(uniform: Uniform, textures: Map<String, Asset<Texture>>): Attribute? =
            when (uniform.name) {
                // Float Attributes
                "_Alpha_Clip_Threshold" -> {
                    if (uniform is FloatUniform) {
                        FloatAttribute(FloatAttribute.AlphaTest, uniform.value ?: 0f)
                    } else null
                }

                "_Metallic" -> {
                    if (uniform is FloatUniform) {
                        // libgdx has no registered Metallic attribute
                        null
                    } else null
                }

                "_Smoothness" -> {
                    if (uniform is FloatUniform) {
                        // libgdx has no registered Smoothness/Roughness attribute
                        null
                    } else null
                }

                "_Normal_Amount" -> {
                    if (uniform is FloatUniform) {
                        // libgdx has no registered NormalScale attribute
                        null
                    } else null
                }

                "_Enable_Emission" -> {
                    if (uniform is FloatUniform) {
//                        val enabled = (uniform.value ?: 0f) > 0.5f
//                        // Use ShininessAttribute as closest match for emission enable (converts to 0 or 1)
//                        FloatAttribute(FloatAttribute.Shininess, if (enabled) 1f else 0f)
                        null
                    } else null
                }

                // Color Attributes
                "_BaseColor" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        ColorAttribute(ColorAttribute.Diffuse, uniform.value)
                    } else null
                }

                "_Emission_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        ColorAttribute(ColorAttribute.Emissive, uniform.value)
                    } else null
                }

                // Texture Attributes
                "_Albedo_Map" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            TextureAttribute(TextureAttribute.Diffuse, this)
                        }
                    } else null
                }

                "_Emission_Map" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            TextureAttribute(TextureAttribute.Emissive, this)
                        }
                    } else null
                }

                "_Normal_Map" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            TextureAttribute(TextureAttribute.Normal, this)
                        }
                    } else null
                }

                else -> null
            }
    }

    fun Map<String, Asset<Texture>>.pick(name: String?, onfound: Texture.() -> Attribute?): Attribute? =
        this[name]?.get()?.let { onfound(it) }
}