package org.roldy.gp.world.manager

import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.AsyncPathfindingManager
import org.roldy.core.pathwalker.TileWalker
import org.roldy.data.state.SquadState
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gp.world.pathfinding.calculateTileWalkCost
import org.roldy.gp.world.utils.Squad
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.pawn.PawnFigure
import org.roldy.rendering.screen.world.WorldScreen

class SquadManager(
    val squad: SquadState,
    val tileLandMarkManagers:List<TileLandmarkManager>,
    private val screen: WorldScreen,
    private val persistentObjects: MutableList<Layered>,
    private val pathfinder: TilePathfinder,
): TileWalker {
    val pathFinderManager = AsyncPathfindingManager(pathfinder::findPath)

    val figure = PawnFigure(this) { tile ->
        calculateTileWalkCost(screen, screen.map)(tile)
    }.apply {
        persistentObjects.add(this)
        //place correct position in world based on coords
        position = worldPosition()
    }

    var nextCoords = squad.leader.coords

    fun teleport(position: Vector2Int) {
        figure.pathWalkerManager.stop()
        coords = position
        figure.position = worldPosition()
        nextCoords = position
    }

    fun worldPosition(): Vector2 =
        screen.map.tilePosition.run {
            resolve(squad.leader.coords).center()
        }

    fun move(position: Vector2Int) {
        pathFinderManager.findPath(nextCoords, position) {
            figure.pathWalking(it)
        }

        if (coords == position)
            onPathEnd(coords)
    }

    val worldPosition get() = figure.position

    override var coords: Vector2Int
        get() = squad.leader.coords
        set(value) {
            Squad.updatePosition(value, squad)
        }

    override val speed: Float
        get() = Squad.calculateSpeed(squad)

    override fun onPathEnd(coords: Vector2Int) {
        nextCoords = coords
        tileLandMarkManagers.forEach {
            it.onTileReached(coords)
        }
    }

    override fun onTileEnter(coords: Vector2Int) {
        this.coords = coords
    }

    override fun onTileLeave(coords: Vector2Int) {
        if (this.coords != coords)
            tileLandMarkManagers.forEach {
                it.onTileExit()
            }
    }

    override fun nextTile(coords: Vector2Int) {
        nextCoords = coords
    }
}