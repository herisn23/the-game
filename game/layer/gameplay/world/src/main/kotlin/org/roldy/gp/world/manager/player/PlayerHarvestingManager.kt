package org.roldy.gp.world.manager.player

import org.roldy.core.Vector2Int
import org.roldy.data.state.GameState
import org.roldy.gp.world.utils.Harvesting
import org.roldy.gp.world.utils.Inventory
import org.roldy.gui.WorldGUI
import org.roldy.gui.widget.*
import kotlin.properties.Delegates
import kotlin.time.Duration

class PlayerHarvestingManager(
    val gui: WorldGUI,
    val gameState: GameState,
    val playerManager: PlayerManager,
) : ProgressingManager {
    var data: Harvesting.Data? = null
    var inProgress: Boolean by Delegates.observable(false) { _, _, newValue ->
        // Reset progress whenever inProgress is set
        data?.let {
            it.harvestable.currentHarvestingProgress = Duration.ZERO
        }
    }
    val hero get() = playerManager.current.squad.leader

    fun findMine(coords: Vector2Int) {
        harvestingWindow {
            Clean(Unit)
            Harvesting.findMine(gameState, coords) {
                with(gui.harvestingWindow) {
                    // Configure harvesting window
                    Open(Unit)
                    State.set(it)
//                    Harvest.set {
//                        inProgress = true
//                    }
                    Collect.set {
                        Inventory.add(hero.inventory, it.harvestable, it.harvested)
                        //TODO update inventory window should be here
                        gui.inventory.inventory.maxSlots = 20
                        gui.inventory.items = hero.inventory.items
                        it.harvested = 0
                    }
                }

                // Configure data for harvesting
                data = Harvesting.Data(
                    it,
                    hero.harvestingSpeed,
                    1f
                ) {
                    it.harvested++
                }
            }
        }

    }

    fun harvestingWindow(run: HarvestingWindowDelegate.() -> Unit) {
        with(gui.harvestingWindow) {
            run()
        }
    }

    private fun stop() {
        inProgress = false
    }

    context(delta: Float)
    override suspend fun update() {
        if (inProgress)
            data?.let {
                Harvesting.harvest(it)
            }
    }

    fun enter(coords: Vector2Int) {
        findMine(coords)
    }

    fun leave() {
        stop()
        data = null
        harvestingWindow {
            Close(Unit)
            Clean(Unit)
        }
    }
}