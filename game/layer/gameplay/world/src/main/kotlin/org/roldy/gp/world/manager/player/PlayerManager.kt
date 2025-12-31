package org.roldy.gp.world.manager.player

import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.ConcurrentLoopConsumer
import org.roldy.data.state.GameState
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gui.WorldGUI
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.screen.world.WorldScreen

class PlayerManager(
    val pathfinder: TilePathfinder,
    val gui: WorldGUI,
    val gameState: GameState,
    val screen: WorldScreen,
    val persistentObjects: MutableList<Layered>
) : ConcurrentLoopConsumer<Float> {
    val progressingManagers = mutableListOf<ProgressingManager>()
    val playerState = gameState.player
    val harvestingManager = PlayerHarvestingManager(gui, gameState, this).apply {
        progressingManagers.add(this)
    }
    val squads = playerState.squads.map {
        SquadManager(
            this,
            it,
            screen,
            persistentObjects,
            pathfinder
        )
    }.toMutableList()

    val current get() = squads[playerState.lastSquad]

    val currentPosition get() = current.worldPosition

    fun move(position: Vector2Int) {
        current.move(position)
    }

    context(delta: Float)
    override suspend fun update() {
        progressingManagers.forEach {
            it.update()
        }
    }
}