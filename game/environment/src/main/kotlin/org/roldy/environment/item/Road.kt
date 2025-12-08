package org.roldy.environment.item

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.renderer.Layered
import org.roldy.environment.MapObjectData

class Road(
    data: MapObjectData,
    atlas: TextureAtlas,
    regionName: String
) : SimpleSpriteObject(data, atlas.findRegion(regionName)) {
    override val layer: Int = Layered.LAYER_1

}