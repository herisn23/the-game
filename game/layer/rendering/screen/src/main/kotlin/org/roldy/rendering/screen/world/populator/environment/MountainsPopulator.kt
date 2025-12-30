package org.roldy.rendering.screen.world.populator.environment

import org.roldy.core.logger
import org.roldy.data.configuration.match
import org.roldy.data.tile.MountainTileData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileObject
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator
import kotlin.random.Random


class MountainsPopulator(override val map: WorldMap) : AutoDisposableAdapter(), WorldChunkPopulator {

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.availableTerrainData(existingObjects)
        return data.mapNotNull { (coords, terrainData) ->
            val random = Random(coords.sum + map.data.seed)
            terrainData.mountainData().filter {
                it.elevation.match(terrainData.noiseData.elevation)
            }.randomOrNull(random)?.let { mountain ->
                SpriteTileObject.Data(
                    layer = Layered.LAYER_4,
                    position = worldPosition(coords),
                    coords = coords,
                    textureRegion = runCatching {
                        terrainData.terrain.biome.atlas.findRegion(mountain.name)
                    }.onFailure {
                        logger.error(it) {
                            mountain.name
                        }
                    }.getOrThrow(),
                    data = mapOf("data" to MountainTileData(coords)),
                )
            }
        }
    }

    fun MapTerrainData.mountainData() =
        terrain.biome.data.mountains
}