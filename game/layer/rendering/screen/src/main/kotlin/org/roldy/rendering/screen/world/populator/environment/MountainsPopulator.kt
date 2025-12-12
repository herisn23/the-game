package org.roldy.rendering.screen.world.populator.environment

import org.roldy.core.Vector2Int
import org.roldy.data.biome.match
import org.roldy.data.tile.TileData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileObject
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator

data class MountainData(
    override val coords: Vector2Int
) : TileData {
    override val walkCost: Float = 0f
}

class MountainsPopulator(override val map: WorldMap) : AutoDisposableAdapter(), WorldChunkPopulator {

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.availableTerrainData(existingObjects)
        return data.mapNotNull { (coords, terrainData) ->
            terrainData.mountainData().find {
                it.elevation.match(terrainData.noiseData.elevation)
            }?.let { mountain ->
                SpriteTileObject.Data(
                    layer = Layered.LAYER_4,
                    name = "mountain", position = worldPosition(coords), coords = coords,
                    textureRegion = terrainData.terrain.biome.atlas.findRegion(mountain.name),
                    data = mapOf("data" to MountainData(coords))
                )
            }
        }
    }

    fun MapTerrainData.mountainData() =
        terrain.biome.data.mountains
}