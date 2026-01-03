package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.mine.harvestable.Gem
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.environment.designer.roughquartz
import org.roldy.rendering.tiles.Decors

class MapAtlas(
    val decors: TextureAtlas,
    val tiles: TextureAtlas,
)


fun SpriteCompositor.composite(
    position: Vector2,
    harvestable: Harvestable,
    biome: BiomeType
): List<Sprite> =
    apply {
        when (harvestable) {
            Gem.Amber -> roughquartz(position, biome)
            else -> texture(position, {
                decors[Decors.mines05]
            }) {
                center()
                parent().center()
            }
        }
    }.retrieve()