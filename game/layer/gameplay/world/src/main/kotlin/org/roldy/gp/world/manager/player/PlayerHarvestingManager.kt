package org.roldy.gp.world.manager.player

import org.roldy.core.Vector2Int
import org.roldy.data.state.GameState
import org.roldy.data.state.HarvestableState
import org.roldy.gp.world.utils.Harvesting
import org.roldy.gp.world.utils.Inventory
import org.roldy.gui.WorldGUI

class PlayerHarvestingManager(
    val gui: WorldGUI,
    val gameState: GameState,
    val playerManager: PlayerManager,
) : ProgressingManager {
    var currentHarvesting: Harvesting.Data? = null

    val hero get() = playerManager.current.squad.leader

    fun start(harvestable: HarvestableState) {
        currentHarvesting = Harvesting.Data(
            harvestable,
            hero.harvestingSpeed,
            1f
        ) {
            harvestable.harvested++
        }
    }

    fun findMine(coords: Vector2Int) {
        gui.harvestingWindow.clean()
        Harvesting.findMine(gameState, coords) {
            gui.harvestingWindow.open()
            gui.harvestingWindow.state = it
            gui.harvestingWindow.onHarvest = {
                start(it)
            }
            gui.harvestingWindow.onCollect = {
                Inventory.add(hero.inventory, it.harvestable, it.harvested)
                //TODO update inventory window should be here
                gui.inventory.inventory.maxSlots = 20
                gui.inventory.items = hero.inventory.items
                it.harvested = 0
            }
        }
    }

    private fun stop() {
        currentHarvesting = null
    }

    context(delta: Float)
    override suspend fun update() {
        currentHarvesting?.let {
            Harvesting.harvest(it)
        }
    }

    fun enter(coords: Vector2Int) {
        findMine(coords)
    }

    fun leave() {
        stop()
        gui.harvestingWindow.close {
            it.clean()
        }
    }
}