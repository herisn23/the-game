package org.roldy.core.shader.attribute

import com.badlogic.gdx.graphics.g3d.Attribute

class BooleanAttribute(type: Long, val enabled: Boolean) : Attribute(type) {
    companion object {
        val useNoiseColor = register("useNoiseColor")
        val leafFlatColor = register("leafFlatColor")
        val trunkFlatColor = register("trunkFlatColor")
        val leafHasNormal = register("leafHasNormal")
        val trunkHasNormal = register("trunkHasNormal")

        fun createUseNoiseColor(enabled: Boolean) =
            BooleanAttribute(useNoiseColor, enabled)

        fun createLeafFlatColor(enabled: Boolean) =
            BooleanAttribute(leafFlatColor, enabled)

        fun createTrunkFlatColor(enabled: Boolean) =
            BooleanAttribute(trunkFlatColor, enabled)

        fun createLeafHasNormal(enabled: Boolean) =
            BooleanAttribute(leafHasNormal, enabled)

        fun createTrunkHasNormal(enabled: Boolean) =
            BooleanAttribute(trunkHasNormal, enabled)
    }

    override fun copy() = BooleanAttribute(type, enabled)
    override fun compareTo(other: Attribute) = 0
    val asInt = if (enabled) 1 else 0
}