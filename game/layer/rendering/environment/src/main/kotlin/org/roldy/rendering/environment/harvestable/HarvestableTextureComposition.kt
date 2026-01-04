package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.mine.harvestable.Gem
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.data.mine.harvestable.Ore
import org.roldy.data.mine.harvestable.Wood
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.environment.designer.*

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
            is Gem -> gem(position, biome)
            is Ore -> when (biome) {
                Cold, ExtremeCold, Forest, Swamp -> orecoldforestswamp(position, biome)
                Savanna, Desert, DeepDesert -> oredesertsavanna(position, biome)
                Jungle -> orejungle(position, biome)
                Volcanic -> orevolcanic(position, biome)
                Water -> {} //Water has no mine
            }

            is Wood -> when (biome) {
                Cold, ExtremeCold -> woodcold(position, biome)
                Forest -> woodforest(position, biome)
                Jungle -> woodjungle(position, biome)
                Savanna -> woodsavanna(position, biome)
                Desert, DeepDesert -> wooddesert(position, biome)
                Swamp -> woodswamp(position, biome)
                Volcanic -> woodvolcanic(position, biome)
                BiomeType.Water -> {}
            }

            else -> test(position, biome)
        }
    }.retrieve()