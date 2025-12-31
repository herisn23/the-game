package org.roldy.gp.world

import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.ConcurrentLoopConsumer
import org.roldy.core.pathwalker.AsyncPathfindingManager
import org.roldy.data.state.GameState
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gp.world.utils.Mining
import org.roldy.gui.WorldGUI
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.pawn.PawnFigure

class PlayerManager(
    val pathfinder: TilePathfinder,
    val gui: WorldGUI,
    val gameState: GameState,
    val camera: OrthographicCamera,
    val map: WorldMap,
    val currentPawn: PawnFigure
) : ConcurrentLoopConsumer<Float> {
    init {
        currentPawn.apply {
            position = map.tilePosition.resolve(data.coords)
        }
        currentPawn.onPathEnd = ::onTileAction
    }


    val pathFinderManager = AsyncPathfindingManager(pathfinder::findPath, currentPawn::coords) { path ->
        currentPawn.pathWalking(path)
    }

    fun focusTile(position: Vector2Int) {
        pathFinderManager.findPath(position)
        if (currentPawn.coords != position)
            movingAwayFromPosition()
        if (currentPawn.coords == position)
            moveToSamePosition()
    }


    fun onTileAction(coords: Vector2Int) {
        findMine(coords)
    }

    fun findMine(coords: Vector2Int) {
        Mining.findMine(gameState, coords) {
            gui.harvestingWindow.open()
            gui.harvestingWindow.mineState = it
            gui.harvestingWindow.onMine = {
                Mining.startMine(it)
            }
        }
    }

    private fun movingAwayFromPosition() {
        gui.harvestingWindow.close {
            it.clean()
        }
    }

    private fun moveToSamePosition() {
        onTileAction(currentPawn.coords)
    }


    context(delta: Float)
    override suspend fun update() {

    }
}