package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.utils.get
import org.roldy.data.tile.MountainTileData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileBehaviour
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator


class MountainsPopulator(
    override val map: WorldMap,
    mountains: List<MountainTileData>,
    val tilesAtlas: TextureAtlas,
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val mountains = mountains.associateBy { it.coords }

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.availableTerrainData(existingObjects)
        return data.mapNotNull { (coords, _) ->
            mountains[coords]?.let { mountain ->
                SpriteTileBehaviour.Data(
                    layer = Layered.LAYER_2,
                    position = worldPosition(coords),
                    coords = coords,
                    textureRegion = tilesAtlas[mountain.data.name],
                    data = mapOf("data" to mountain)
                )
            }
        }
    }
}