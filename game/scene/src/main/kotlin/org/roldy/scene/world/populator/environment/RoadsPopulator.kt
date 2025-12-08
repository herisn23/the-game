package org.roldy.scene.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.environment.MapObjectData
import org.roldy.environment.item.Road
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.pathfinding.Pathfinder
import org.roldy.scene.world.populator.RoadsGenerator
import org.roldy.scene.world.populator.WorldChunkPopulator

class RoadsPopulator(
    override val map: WorldMap,
    pathfinder: Pathfinder,
    settlements: List<SettlementData>,
) : AutoDisposableAdapter(), WorldChunkPopulator {

    val roads = RoadsGenerator.generate(settlements, map.terrainData, map.mapSize)

    val atlas by disposable { TextureAtlas(loadAsset("environment/Roads.atlas")) }
    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<MapObjectData>
    ): List<MapObjectData> {
        val data = chunk.data()
        val roadsInChunk = roads.filter {
            data.contains(it.position)
        }
        return roadsInChunk.map { road ->
            val position = worldPosition(road.position)
            MapObjectData(name = "road", position = position, road.position) {
                Road(it, atlas, "hexRoad-001001-00")
            }
        }
    }
}