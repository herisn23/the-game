package org.roldy.core.shader.attribute

import com.badlogic.gdx.graphics.g3d.Attribute

class FloatValueAttribute(type: Long, val value: Float) : Attribute(type) {
    companion object {
        val smallFreq = register("smallFreq")
        val largeFreq = register("largeFreq")
        val leafMetallic = register("leafMetallic")
        val leafSmoothness = register("leafSmoothness")
        val trunkMetallic = register("trunkMetallic")
        val trunkSmoothness = register("trunkSmoothness")
        val leafNormalStrength = register("leafNormalStrength")
        val trunkNormalStrength = register("trunkNormalStrength")

        fun createSmallFreq(freq: Float) = FloatValueAttribute(smallFreq, freq)
        fun createLargeFreq(freq: Float) = FloatValueAttribute(largeFreq, freq)
        fun createLeafMetallic(freq: Float) = FloatValueAttribute(leafMetallic, freq)
        fun createLeafSmoothness(freq: Float) = FloatValueAttribute(leafSmoothness, freq)
        fun createTrunkMetallic(freq: Float) = FloatValueAttribute(trunkMetallic, freq)
        fun createTrunkSmoothness(freq: Float) = FloatValueAttribute(trunkSmoothness, freq)
        fun createLeafNormalStrength(freq: Float) = FloatValueAttribute(leafNormalStrength, freq)
        fun createTrunkNormalStrength(freq: Float) = FloatValueAttribute(trunkNormalStrength, freq)
    }

    override fun copy(): Attribute {
        return FloatValueAttribute(type, value)
    }

    override fun compareTo(other: Attribute): Int = 0
}