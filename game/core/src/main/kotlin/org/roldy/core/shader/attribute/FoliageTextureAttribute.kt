package org.roldy.core.shader.attribute

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute

class FoliageTextureAttribute(type: Long, val texture: Texture) : Attribute(type) {
    companion object {
        val leafTexture = register("leafTexture")
        val leafNormal = register("leafNormal")
        val trunkTexture = register("trunkTexture")
        val trunkNormal = register("trunkNormal")

        fun createLeafTexture(texture: Texture) =
            FoliageTextureAttribute(leafTexture, texture)

        fun createTrunkTexture(texture: Texture) =
            FoliageTextureAttribute(trunkTexture, texture)

        fun createLeafNormal(texture: Texture) =
            FoliageTextureAttribute(leafNormal, texture)

        fun createTrunkNormal(texture: Texture) =
            FoliageTextureAttribute(trunkNormal, texture)
    }

    override fun copy() = FoliageTextureAttribute(type, texture)
    override fun compareTo(other: Attribute) = 0
}
