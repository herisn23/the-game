package org.roldy.environment.item

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.renderer.Layered
import org.roldy.environment.MapObjectData

class Road(
    data: MapObjectData,
    atlas: TextureAtlas,
    tileSize: Int
) : SimpleSpriteObject(data, atlas.findRegion("road"), {
    setSize(tileSize.toFloat(), tileSize.toFloat())
    setPosition(data.position.x, data.position.y)
}) {
    override val layer: Int = Layered.LAYER_1

}