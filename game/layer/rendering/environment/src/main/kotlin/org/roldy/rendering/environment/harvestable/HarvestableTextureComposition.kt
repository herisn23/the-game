package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import org.roldy.data.map.Terrain
import org.roldy.data.mine.HarvestableType
import org.roldy.data.state.HarvestableState
import org.roldy.rendering.environment.composite.CompositeSprite
import org.roldy.rendering.environment.composite.SpriteCompositor

class MapAtlas(
    val environment: TextureAtlas,
    val tiles: TextureAtlas,
)


fun SpriteCompositor.composite(
    position: Vector2,
    harvestable: HarvestableState,
    terrain: Terrain
): List<CompositeSprite> =
    apply {
        when (harvestable.harvestable.type) {
            HarvestableType.ORE -> ore(
                position,
                terrain,
                harvestable.harvestable
            ) { harvestable.refreshing.supplies == 0 }

            HarvestableType.WOOD -> wood(
                position,
                terrain,
                harvestable.harvestable
            ) { harvestable.refreshing.supplies == 0 }

            HarvestableType.FIBER -> wood(
                position,
                terrain,
                harvestable.harvestable
            ) { harvestable.refreshing.supplies == 0 }

            HarvestableType.GEM -> gem(
                position,
                terrain,
                harvestable.harvestable
            ) { harvestable.refreshing.supplies == 0 }

            HarvestableType.LEATHER -> wood(
                position,
                terrain,
                harvestable.harvestable
            ) { harvestable.refreshing.supplies == 0 }
        }

    }.retrieve()