package org.roldy.scene.world.populator

import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.terrain.TileData

interface WorldPopulator {
    val worldMap: WorldMap
    fun worldPosition(coords: Vector2Int, offsetCorrection: Boolean = false): Vector2 =
        worldMap.tilePosition.resolve(coords, offsetCorrection)


    fun WorldMapChunk.data(): Map<Vector2Int, TileData> =
        tilesCoords.mapNotNull { coords ->
            worldMap.terrainData[coords]?.let {
                coords to it
            }
        }.toMap()
}