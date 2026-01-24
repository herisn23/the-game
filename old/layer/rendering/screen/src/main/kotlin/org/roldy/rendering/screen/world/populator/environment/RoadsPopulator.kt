package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.logger
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.tile.RoadTileData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileBehaviour
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator


class RoadsPopulator(
    override val map: WorldMap,
    val atlas: TextureAtlas,
    private val roads: List<RoadTileData>
) : WorldChunkPopulator {
    val logger by logger()
    val roadTypes = mapOf(
//        BiomeType.Volcanic to "dirt1",
//        BiomeType.Volcanic to "dirt2",
        BiomeType.MysticalForest to "dirt3",
        BiomeType.Forest to "dirt4",
        BiomeType.Cold to "snow",
        BiomeType.WitchForest to "stone1",
        BiomeType.Swamp to "stone2",
//        BiomeType.Volcanic to "stone3",
//        BiomeType.Volcanic to "stone4"
    )
    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val chunkData = chunk.terrainData()
        val roadsInChunk = roads.filter { road ->
            chunkData.contains(road.node.coords)
        }

        return roadsInChunk.map { road ->
            val terrain = chunkData.getValue(road.node.coords)
            val position = worldPosition(road.node.coords)
            SpriteTileBehaviour.Data(
                position = position,
                coords = road.node.coords,
                textureRegion = getRoadTexture(road.bitmask, terrain.terrain.biome.data.type),
                layer = Layered.LAYER_1,
                data = mapOf("road" to road)
            )
        }
    }

    /**
     * Gets texture region for road tile based on connections.
     * Uses bitmask naming: hexRoad-{6-bit}-{2-digit-variant}
     */
    private fun getRoadTexture(bitmask: String, biome: BiomeType): TextureRegion {

        val regionName = "${roadTypes[biome]}_$bitmask"

        return atlas.findRegion(regionName)
            ?: error("No road texture found in atlas [$regionName]")
    }

}