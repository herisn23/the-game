package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.composite.SpriteCompositor

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

    }.retrieve()