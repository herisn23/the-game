package org.roldy.core.stream

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class RenderItem(
    val sprite: Sprite
) {
    var data: MapItemData? = null
    fun bind(data: MapItemData, atlas: TextureAtlas) {
        this.data = data
        val region = atlas.findRegion(data.type)
        sprite.setRegion(region)
        sprite.setCenter(data.x, data.y)
        sprite.setOriginCenter()
        sprite.setScale(1f)
        sprite.setRotation(0f)
    }
}

class RenderItemPool(
    private val atlas: TextureAtlas
) : com.badlogic.gdx.utils.Pool<RenderItem>() {
    override fun newObject(): RenderItem {
        val region = atlas.findRegion("tile000")
        return RenderItem(Sprite(region))
    }
}