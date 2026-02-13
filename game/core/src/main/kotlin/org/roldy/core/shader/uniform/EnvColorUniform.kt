package org.roldy.core.shader.uniform

import com.badlogic.gdx.graphics.Color

data class EnvColorUniform(override val id: String, val color: Color) : UniformValue {
    companion object {
        val leafBaseColor = "leafBaseColor"
        val leafNoiseColor = "leafNoiseColor"
        val leafNoiseLargeColor = "leafNoiseLargeColor"
        val trunkBaseColor = "trunkBaseColor"
        val trunkNoiseColor = "trunkNoiseColor"
        val trunkEmissiveColor = "trunkEmissiveColor"
        val frostingColor = "frostingColor"
        val emissiveColor = "emissiveColor"
        val emissive2Color = "emissive2Color"

        fun createLeafBaseColor(color: Color) = EnvColorUniform(leafBaseColor, color)
        fun createLeafNoiseColor(color: Color) = EnvColorUniform(leafNoiseColor, color)
        fun createLeafNoiseLargeColor(color: Color) = EnvColorUniform(leafNoiseLargeColor, color)
        fun createTrunkBaseColor(color: Color) = EnvColorUniform(trunkBaseColor, color)
        fun createTrunkNoiseColor(color: Color) = EnvColorUniform(trunkNoiseColor, color)
        fun createTrunkEmissiveColor(color: Color) = EnvColorUniform(trunkEmissiveColor, color)
        fun createFrostingColor(color: Color) = EnvColorUniform(frostingColor, color)
        fun createEmissiveColor(color: Color) = EnvColorUniform(emissiveColor, color)
        fun createEmissive2Color(color: Color) = EnvColorUniform(emissive2Color, color)
    }
}