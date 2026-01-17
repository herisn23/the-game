package org.roldy.gp.world.manager.player

import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.ConcurrentLoopConsumer
import org.roldy.core.utils.sequencer
import org.roldy.data.state.GameState
import org.roldy.gp.world.manager.SquadManager
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gp.world.utils.Inventory
import org.roldy.gp.world.utils.Settlement
import org.roldy.gui.WorldGUI
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.gui.el.onClick
import org.roldy.rendering.screen.world.WorldScreen

class PlayerManager(
    val pathfinder: TilePathfinder,
    val gui: WorldGUI,
    val gameState: GameState,
    val screen: WorldScreen,
    val persistentObjects: MutableList<Layered>
) : ConcurrentLoopConsumer<Float> {

    val settlements by sequencer { gameState.settlements }
    init {
        gui.inventoryButton.onClick {
            gui.inventory.open(current.squad.leader, Inventory.size(current.squad.leader))
        }
        gui.teleportToStart.onClick {
            current.teleport(settlements.next().coords)
        }
        gui.teleportToEnd.onClick {
            current.teleport(screen.map.terrainData.keys.random())
        }
    }

    val progressingManagers = mutableListOf<ProgressingManager>()
    val playerState = gameState.player

    val harvestingManager = PlayerHarvestingManager(gui, gameState, this).apply {
        progressingManagers.add(this)
    }

    val squads = playerState.squads.map {
        SquadManager(
            it,
            listOf(harvestingManager),
            screen,
            persistentObjects,
            pathfinder
        )
    }.toMutableList()

    val current get() = squads[playerState.lastSquad]

    val currentPosition get() = current.worldPosition

    fun move(position: Vector2Int) {
        current.move(position)
        Settlement.find(gameState, position) {
            println("Settlement found on $position")
        }
    }

    context(delta: Float)
    override suspend fun update() {
        progressingManagers.forEach {
            it.update()
        }
    }
}