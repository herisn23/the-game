package org.roldy.environment.item

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.environment.MapObjectData

class Settlement(
    data: MapObjectData,
    atlas: TextureAtlas
) : SimpleSpriteObject(data, atlas.findRegion("house"))