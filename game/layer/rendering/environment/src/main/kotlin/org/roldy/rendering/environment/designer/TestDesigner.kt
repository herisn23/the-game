package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Tiles

fun SpriteCompositor.test(position: Vector2, biomeType: BiomeType) {
    texture(position, { tiles[Tiles.hexPlainsFarmBarnWood00] }) {
//        centered()
//        left()
//        offset(x - tileSize/2f, y)
    }
}