package org.roldy.gp.world.manager

import org.roldy.core.Vector2Int
import org.roldy.data.state.GameState
import org.roldy.data.state.HarvestableState
import org.roldy.data.state.HeroState
import org.roldy.gp.world.manager.player.ProgressingManager
import org.roldy.gp.world.utils.Harvesting
import org.roldy.gp.world.utils.Inventory
import kotlin.properties.Delegates
import kotlin.time.Duration

abstract class HarvestingManager(
    val gameState: GameState
) : ProgressingManager, TileLandmarkManager {
    var data: Harvesting.Data? = null
    abstract val hero: HeroState

    private var inProgress: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (!newValue) {
            data?.let {
                it.harvestable.currentHarvestingProgress = Duration.ZERO
            }
        }
    }

    override fun onTileReached(coords: Vector2Int) {
        Harvesting.find(gameState, coords) { state ->
            data = Harvesting.Data(
                state,
                hero.harvestingSpeed,
                1f
            ) {
                state.harvested++
            }
            updateProgress(Harvesting.calculateProgress(data!!))
            harvestableFound(state)
        }
    }

    fun collect(
        onInventoryFull: () -> Unit = {},
        onCollected: () -> Unit = {}
    ) {
        data?.let { data ->
            Inventory.add(hero, data.harvestable.harvestable, data.harvestable.harvested, onInventoryFull) {
                data.harvestable.harvested = 0
                onCollected()
            }
        }
    }

    /**
     * This function is called when state for tile is found
     */
    abstract fun harvestableFound(state: HarvestableState)

    context(delta: Float)
    override suspend fun update() {
        if (inProgress)
            data?.let {
                inProgress = Harvesting.harvest(it, ::updateProgress)
            }

    }

    abstract fun updateProgress(progress: Float)

    fun start() {
        inProgress = true
    }

    fun stop() {
        inProgress = false

    }

    override fun onTileExit() {
        stop()
        data = null
        onLeave()
    }

    abstract fun onLeave()
}