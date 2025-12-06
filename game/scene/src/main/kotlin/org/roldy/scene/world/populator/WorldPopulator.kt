package org.roldy.scene.world.populator

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.Vector2Int
import org.roldy.core.asset.loadAsset
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.environment.MapObjectData
import org.roldy.environment.item.Road
import org.roldy.environment.item.Settlement
import org.roldy.map.WorldMapSize
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.populator.environment.FoliagePopulator
import org.roldy.scene.world.populator.environment.RoadsGenerator
import org.roldy.scene.world.populator.environment.SettlementsGenerator
import org.roldy.terrain.TileData

class WorldPopulator(
    private val terrainData: Map<Vector2Int, TileData>,
    private val mapSize: WorldMapSize,
    private val seed: Long
) : ChunkPopulator<MapObjectData, WorldMapChunk> {

    val trees = TextureAtlas(loadAsset("Road.atlas"))
    val houseAtlas = TextureAtlas(loadAsset("House.atlas"))
    val settlements = SettlementsGenerator.generate(terrainData, mapSize, seed)
    val roads = RoadsGenerator.generate(settlements, terrainData, mapSize)

    val populators: List<WorldChunkPopulator> = listOf(
        FoliagePopulator(seed)
    )

    override fun populate(
        chunk: WorldMapChunk
    ): List<MapObjectData> =
        mutableListOf<MapObjectData>().apply {
            val data = terrainData.filterBy(chunk)
            val settlementsInChunk = settlements.filter { settlement ->
                data.contains(settlement.coords)
            }
            val roadsInChunk = roads.filter {
                data.contains(it)
            }
            this += roadsInChunk.map { road ->
                val position = chunk.objectPosition(road)
                MapObjectData(name = "road", position = position, road) {
                    Road(it, trees, chunk.tileSize)
                }
            }
            this += settlementsInChunk.map { settle ->
                val position = chunk.objectPosition(settle.coords)
                MapObjectData(name = settle.name, position = position, settle.coords) {
                    Settlement(it, houseAtlas)
                }
            }
            this += populators.flatMap { it.populate(chunk, this) }
        }


    private fun Map<Vector2Int, TileData>.filterBy(
        chunk: WorldMapChunk
    ) = run {
        chunk.tilesCoords.mapNotNull { coords ->
            this[coords]?.let {
                coords to it
            }
        }.toMap()
    }
}