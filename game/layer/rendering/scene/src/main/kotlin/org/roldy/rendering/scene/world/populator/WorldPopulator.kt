package org.roldy.rendering.scene.world.populator

import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.rendering.map.TileData
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.scene.world.chunk.WorldMapChunk

interface WorldPopulator {
    val map: WorldMap
    fun worldPosition(coords: Vector2Int, offsetCorrection: Boolean = false): Vector2 =
        map.tilePosition.resolve(coords, offsetCorrection)

    fun WorldMapChunk.data(): Map<Vector2Int, TileData> =
        tilesCoords.mapNotNull { coords ->
            map.terrainData[coords]?.let {
                coords to it
            }
        }.toMap()
}