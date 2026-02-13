package org.roldy.core.shader.uniform

import com.badlogic.gdx.graphics.Texture
import org.roldy.core.asset.Asset
import org.roldy.core.configuration.data.ColorType
import org.roldy.core.configuration.data.FloatUniform
import org.roldy.core.configuration.data.TexEnvType
import org.roldy.core.configuration.data.Uniform

object UniformMapper {

    object Foliage {

        fun map(uniform: Uniform, textures: Map<String, Asset<Texture>>): UniformValue? =
            when (uniform.name) {
                // Boolean Attributes
                "_Use_Vertex_Color_Wind" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createUseVertexColorWind(enabled)
                    } else null
                }

                "_Use_Color_Noise" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createUseNoiseColor(enabled)
                    } else null
                }

                "_Enable_Leaf_Normal" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createLeafHasNormal(enabled)
                    } else null
                }

                "_Enable_Trunk_Normal" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createTrunkHasNormal(enabled)
                    } else null
                }

                "_Leaf_Flat_Color" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createLeafFlatColor(enabled)
                    } else null
                }

                "_Trunk_Flat_Color_Switch" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createTrunkFlatColor(enabled)
                    } else null
                }

                "_Frosting_Use_World_Normals" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createEnableFrosting(enabled)
                    } else null
                }

                "_Enable_Frosting" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createEnableFrosting(enabled)
                    } else null
                }

                "_Enable_Emission" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createEnableEmission(enabled)
                    } else null
                }

                "_Enable_Pulse" -> {
                    if (uniform is FloatUniform) {
                        val enabled = (uniform.value ?: 0f) > 0.5f
                        BooleanUniform.createEnablePulse(enabled)
                    } else null
                }

                // Float Attributes
                "_Color_Noise_Small_Freq" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createSmallFreq(uniform.value ?: 0f)
                    } else null
                }

                "_Color_Noise_Large_Freq" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createLargeFreq(uniform.value ?: 0f)
                    } else null
                }

                "_Leaf_Normal_Strength" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createLeafNormalStrength(uniform.value ?: 0f)
                    } else null
                }

                "_Leaf_Metallic" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createLeafMetallic(uniform.value ?: 0f)
                    } else null
                }

                "_Leaf_Smoothness" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createLeafSmoothness(uniform.value ?: 0f)
                    } else null
                }

                "_Trunk_Normal_Strength" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createTrunkNormalStrength(uniform.value ?: 0f)
                    } else null
                }

                "_Trunk_Metallic" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createTrunkMetallic(uniform.value ?: 0f)
                    } else null
                }

                "_Trunk_Smoothness" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createTrunkSmoothness(uniform.value ?: 0f)
                    } else null
                }

                "_Frosting_Falloff" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createFrostingFalloff(uniform.value ?: 0f)
                    } else null
                }

                "_Frosting_Height" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createFrostingHeight(uniform.value ?: 0f)
                    } else null
                }

                "_Emissive_Amount" -> {
                    if (uniform is FloatUniform) {
                        FloatValueUniform.createEmissiveAmount(uniform.value ?: 0f)
                    } else null
                }

                // Texture Attributes
                "_Leaf_Texture" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createLeafTexture(this)
                        }
                    } else null
                }

                "_Leaf_Normal" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createLeafNormal(this)
                        }
                    } else null
                }

                "_Trunk_Texture" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createTrunkTexture(this)
                        }
                    } else null
                }

                "_Trunk_Normal" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createTrunkNormal(this)
                        }
                    } else null
                }

                "_Trunk_Emissive_Mask" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createTrunkEmissiveMask(this)
                        }
                    } else null
                }

                "_Emissive_Mask" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createEmissiveMask(this)
                        }
                    } else null
                }

                "_Emissive_2_Mask" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createEmissive2Mask(this)
                        }
                    } else null
                }

                "_Emissive_Pulse_Map" -> {
                    if (uniform is TexEnvType && uniform.value != null) {
                        textures.pick(uniform.value) {
                            EnvTextureUniform.createEmissivePulseMap(this)
                        }
                    } else null
                }

                // Color Attributes
                "_Leaf_Noise_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createLeafNoiseColor(uniform.value)
                    } else null
                }

                "_Leaf_Base_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createLeafBaseColor(uniform.value)
                    } else null
                }

                "_Leaf_Noise_Large_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createLeafNoiseLargeColor(uniform.value)
                    } else null
                }

                "_Trunk_Base_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createTrunkBaseColor(uniform.value)
                    } else null
                }

                "_Trunk_Noise_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createTrunkNoiseColor(uniform.value)
                    } else null
                }

                "_Trunk_Emissive_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createTrunkEmissiveColor(uniform.value)
                    } else null
                }

                "_Frosting_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createFrostingColor(uniform.value)
                    } else null
                }

                "_Emissive_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createEmissiveColor(uniform.value)
                    } else null
                }

                "_Emissive_2_Color" -> {
                    if (uniform is ColorType && uniform.value != null) {
                        EnvColorUniform.createEmissive2Color(uniform.value)
                    } else null
                }

                else -> null
            }
    }

    fun Map<String, Asset<Texture>>.pick(name: String?, onfound: Texture.() -> EnvTextureUniform) =
        this[name]?.get()?.onfound()
}
