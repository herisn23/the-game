package org.roldy.scene.world.populator

import com.badlogic.gdx.utils.Disposable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposableList
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.environment.MapObjectData
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.populator.environment.FoliagePopulator
import org.roldy.scene.world.populator.environment.SettlementPopulator

class WorldMapPopulator(
    override val worldMap: WorldMap,
) : AutoDisposableAdapter(), ChunkPopulator<MapObjectData, WorldMapChunk>, Disposable, WorldPopulator {

//    val trees = TextureAtlas(loadAsset("Road.atlas"))
//    val houseAtlas by disposable {TextureAtlas(loadAsset("House.atlas"))}


//    val roads = RoadsGenerator.generate(settlements, terrainData, mapSize)
//

    val populators: List<WorldChunkPopulator> = disposableList(
        SettlementPopulator(worldMap),
        FoliagePopulator(worldMap)
    )

    override fun populate(
        chunk: WorldMapChunk
    ): List<MapObjectData> =
        mutableListOf<MapObjectData>().apply {

//            val roadsInChunk = roads.filter {
//                data.contains(it.position)
//            }
//            this += roadsInChunk.map { road ->
//                val position = chunk.worldPosition(road.position)
//                MapObjectData(name = "road", position = position, road.position) {
//                    Road(it, trees, chunk.tileSize)
//                }
//            }
            this += populators.flatMap { it.populate(chunk, this) }
        }
}