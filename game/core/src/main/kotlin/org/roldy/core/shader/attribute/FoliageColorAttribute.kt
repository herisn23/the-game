package org.roldy.core.shader.attribute

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Attribute

class FoliageColorAttribute(type: Long, val color: Color) : Attribute(type) {

    companion object {
        val leafBaseColor = register("leafBaseColor")
        val leafNoiseColor = register("leafNoiseColor")
        val leafNoiseLargeColor = register("leafNoiseLargeColor")


        val trunkBaseColor = register("trunkBaseColor")
        val trunkNoiseColor = register("trunkNoiseColor")

        fun createLeafBaseColor(color: Color) =
            FoliageColorAttribute(leafBaseColor, color)

        fun createLeafNoiseColor(color: Color) =
            FoliageColorAttribute(leafNoiseColor, color)

        fun createLeafNoiseLargeColor(color: Color) =
            FoliageColorAttribute(leafNoiseLargeColor, color)

        fun createTrunkBaseColor(color: Color) =
            FoliageColorAttribute(trunkBaseColor, color)

        fun createTrunkNoiseColor(color: Color) =
            FoliageColorAttribute(trunkNoiseColor, color)
    }

    override fun copy(): Attribute {
        return FoliageColorAttribute(type, color)
    }

    override fun compareTo(other: Attribute): Int = 0
}