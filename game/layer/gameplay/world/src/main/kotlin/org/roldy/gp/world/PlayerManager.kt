package org.roldy.gp.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.AsyncPathfindingManager
import org.roldy.core.utils.project
import org.roldy.core.x
import org.roldy.data.state.GameState
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gui.WorldGUI
import org.roldy.gui.general.popup.data.minePopupContent
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.pawn.PawnFigure

class PlayerManager(
    val pathfinder: TilePathfinder,
    val gui: WorldGUI,
    val gameState: GameState,
    val camera: OrthographicCamera,
    val map: WorldMap,
    val currentPawn: PawnFigure
) {
    init {
        currentPawn.apply {
            position = map.tilePosition.resolve(data.coords)
        }
    }

    var lastFocus: Vector2Int? = null
    val pathFinderManager = AsyncPathfindingManager(pathfinder::findPath, currentPawn::coords) { path ->
        currentPawn.pathWalking(path)
    }

    fun moveTo(position: Vector2Int) {
        pathFinderManager.findPath(position)
    }

    fun tileFocus(coords: Vector2Int) {
        //TODO this function shouldn't be here

        gui.hideTileInfo()

        if (lastFocus != null && lastFocus == coords) {
            lastFocus = null
            return
        }

        lastFocus = coords

        fun follow(): Vector2Int {
            val world = map.tilePosition.resolve(coords)
            camera.project(world)
            val flippedY = Gdx.graphics.height - world.y
            return world.x.toInt() x flippedY.toInt()
        }


        val mine = gameState.mines.find { mine -> mine.coords == coords }
        mine?.let {
            gui
                .showTileInfo(
                    { minePopupContent(it, mine) },
                    ::follow
                )
        }

    }
}