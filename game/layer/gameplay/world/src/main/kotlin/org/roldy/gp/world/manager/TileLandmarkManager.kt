package org.roldy.gp.world.manager

import org.roldy.core.Vector2Int

interface TileLandmarkManager {

    /**
     * This function is called when entity land on tile by path walking
     */
    fun onTileReached(coords: Vector2Int)

    /**
     * Called when entity get away from current tile
     */
    fun onTileExit()
}