package org.roldy.gp.world.pathfinding

import org.roldy.core.Vector2Int
import org.roldy.data.tile.walkCost
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.WorldScreen


fun calculateTileWalkCost(screen: WorldScreen, worldMap: WorldMap): (tile: Vector2Int) -> Float =
    { tile ->
        val objectsData = screen.chunkManager.tileData(tile)
        val terrainData = listOfNotNull(worldMap.terrainData[tile])
        val tileData = objectsData + terrainData
        tileData.walkCost()
    }