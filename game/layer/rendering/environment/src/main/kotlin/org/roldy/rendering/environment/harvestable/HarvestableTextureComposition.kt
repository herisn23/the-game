package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.state.HarvestableState
import org.roldy.rendering.environment.composite.SpriteCompositor

class MapAtlas(
    val environment: TextureAtlas,
    val tiles: TextureAtlas,
)


fun SpriteCompositor.composite(
    position: Vector2,
    harvestable: HarvestableState,
    biome: BiomeType
): List<Pair<Sprite, () -> Boolean>> =
    apply {
        ore(position, biome, harvestable.harvestable) { harvestable.refreshing.supplies == 0 }
    }.retrieve()