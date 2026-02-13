package org.roldy.core.shader.uniform

data class BooleanUniform(override val id: String, val enabled: Boolean) : UniformValue {
    companion object {
        val useNoiseColor = "useNoiseColor"
        val leafFlatColor = "leafFlatColor"
        val trunkFlatColor = "trunkFlatColor"
        val leafHasNormal = "leafHasNormal"
        val trunkHasNormal = "trunkHasNormal"
        val useVertexColorWind = "useVertexColorWind"
        val enableFrosting = "enableFrosting"
        val enableEmission = "enableEmission"
        val enablePulse = "enablePulse"

        fun createUseNoiseColor(enabled: Boolean) = BooleanUniform(useNoiseColor, enabled)
        fun createLeafFlatColor(enabled: Boolean) = BooleanUniform(leafFlatColor, enabled)
        fun createTrunkFlatColor(enabled: Boolean) = BooleanUniform(trunkFlatColor, enabled)
        fun createLeafHasNormal(enabled: Boolean) = BooleanUniform(leafHasNormal, enabled)
        fun createTrunkHasNormal(enabled: Boolean) = BooleanUniform(trunkHasNormal, enabled)
        fun createUseVertexColorWind(enabled: Boolean) = BooleanUniform(useVertexColorWind, enabled)
        fun createEnableFrosting(enabled: Boolean) = BooleanUniform(enableFrosting, enabled)
        fun createEnableEmission(enabled: Boolean) = BooleanUniform(enableEmission, enabled)
        fun createEnablePulse(enabled: Boolean) = BooleanUniform(enablePulse, enabled)
    }

    val int = if (enabled) 1 else 0
}