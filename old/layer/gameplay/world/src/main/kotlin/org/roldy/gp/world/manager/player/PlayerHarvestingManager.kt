package org.roldy.gp.world.manager.player

import org.roldy.data.state.GameState
import org.roldy.data.state.HarvestableState
import org.roldy.gp.world.manager.HarvestingManager
import org.roldy.gui.WorldGUI

class PlayerHarvestingManager(
    val gui: WorldGUI,
    gameState: GameState,
    val playerManager: PlayerManager,
) : HarvestingManager(gameState) {
    override val hero get() = playerManager.current.squad.leader


    override fun harvestableFound(state: HarvestableState) {
        gui.harvestingWindow {
            // Configure harvesting window
            open(Unit)
            this.state.set(state)

            harvest.set {
                start()
            }
            collect.set {
                this@PlayerHarvestingManager.collect {
                    gui.inventory.refresh()
                }
            }
        }
    }

    override fun updateProgress(progress: Float) {
        gui.harvestingWindow {
            harvestingProgress set progress
        }
    }

    override fun onLeave() {
        gui.harvestingWindow {
            close(Unit)
            clean(Unit)
        }
    }
}