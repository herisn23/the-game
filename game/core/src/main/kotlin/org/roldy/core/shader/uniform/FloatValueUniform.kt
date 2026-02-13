package org.roldy.core.shader.uniform


data class FloatValueUniform(override val id: String, val value: Float) : UniformValue {
    companion object {
        val smallFreq = "smallFreq"
        val largeFreq = "largeFreq"
        val leafMetallic = "leafMetallic"
        val leafSmoothness = "leafSmoothness"
        val trunkMetallic = "trunkMetallic"
        val trunkSmoothness = "trunkSmoothness"
        val leafNormalStrength = "leafNormalStrength"
        val trunkNormalStrength = "trunkNormalStrength"

        val frostingFalloff = "frostingFalloff"
        val frostingHeight = "frostingHeight"
        val emissiveAmount = "emissiveAmount"

        fun createSmallFreq(freq: Float) = FloatValueUniform(smallFreq, freq)
        fun createLargeFreq(freq: Float) = FloatValueUniform(largeFreq, freq)
        fun createLeafMetallic(freq: Float) = FloatValueUniform(leafMetallic, freq)
        fun createLeafSmoothness(freq: Float) = FloatValueUniform(leafSmoothness, freq)
        fun createTrunkMetallic(freq: Float) = FloatValueUniform(trunkMetallic, freq)
        fun createTrunkSmoothness(freq: Float) = FloatValueUniform(trunkSmoothness, freq)
        fun createLeafNormalStrength(freq: Float) = FloatValueUniform(leafNormalStrength, freq)
        fun createTrunkNormalStrength(freq: Float) = FloatValueUniform(trunkNormalStrength, freq)
        fun createFrostingFalloff(freq: Float) = FloatValueUniform(frostingFalloff, freq)
        fun createFrostingHeight(freq: Float) = FloatValueUniform(frostingHeight, freq)
        fun createEmissiveAmount(freq: Float) = FloatValueUniform(emissiveAmount, freq)
    }
}