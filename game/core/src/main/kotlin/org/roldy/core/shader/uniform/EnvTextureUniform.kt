package org.roldy.core.shader.uniform

import com.badlogic.gdx.graphics.Texture

data class EnvTextureUniform(override val id: String, val texture: Texture) : UniformValue {
    companion object {
        val leafTexture = "leafTexture"
        val leafNormal = "leafNormal"
        val trunkTexture = "trunkTexture"
        val trunkNormal = "trunkNormal"
        val trunkEmissiveMask = "trunkEmissiveMask"
        val emissiveMask = "emissiveMask"
        val emissive2Mask = "emissive2Mask"
        val emissivePulseMap = "emissivePulseMap"

        fun createLeafTexture(texture: Texture) = EnvTextureUniform(leafTexture, texture)
        fun createTrunkTexture(texture: Texture) = EnvTextureUniform(trunkTexture, texture)
        fun createLeafNormal(texture: Texture) = EnvTextureUniform(leafNormal, texture)
        fun createTrunkNormal(texture: Texture) = EnvTextureUniform(trunkNormal, texture)
        fun createTrunkEmissiveMask(texture: Texture) = EnvTextureUniform(trunkEmissiveMask, texture)
        fun createEmissiveMask(texture: Texture) = EnvTextureUniform(emissiveMask, texture)
        fun createEmissive2Mask(texture: Texture) = EnvTextureUniform(emissive2Mask, texture)
        fun createEmissivePulseMap(texture: Texture) = EnvTextureUniform(emissivePulseMap, texture)
    }
}
