package org.roldy.gp.world.utils

import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.async
import org.roldy.core.utils.progress
import org.roldy.core.utils.toDuration
import org.roldy.data.state.GameState
import org.roldy.data.state.HarvestableState
import org.roldy.data.state.RefreshingState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object Harvesting {

    data class Data(
        val harvestable: HarvestableState,
        val harvestSpeed: Duration,
        val probability: Float,
        val onHarvest: () -> Unit
    )

    fun findMine(
        state: GameState, coords: Vector2Int,
        onMineFound: (HarvestableState) -> Unit
    ) {
        async { main ->
            val foundMine = state.mines.find { mine -> mine.coords == coords }
            main {
                foundMine?.let {
                    onMineFound(foundMine)
                }
            }
        }
    }

    context(delta: Float)
    fun refresh(state: RefreshingState) {
        with(state) {
            if (supplies < max) {
                // Accumulate time (data is delta time in seconds)
                currentRefreshTime += delta.toDouble().seconds

                // Refresh while we have enough time and haven't reached max
                if (currentRefreshTime >= timeToRefresh && supplies < max) {
                    supplies++
                    currentRefreshTime = 0.seconds
                }
            } else {
                // Reset refresh timer when at max capacity
                currentRefreshTime = 0.seconds
            }
        }
    }

    fun calculateProgress(data: Data) =
        data.harvestable.currentHarvestingProgress.progress(data.harvestSpeed, 0f, 1f)

    /**
     * Harvest resource
     * @return True if harvested, False when supplies running out
     */
    context(delta: Float)
    fun harvest(
        data: Data,
        progress: (Float) -> Unit = {}
    ): Boolean = with(data.harvestable.refreshing) {
        if (supplies > 0) {
            data.harvestable.currentHarvestingProgress += delta.toDuration()
            progress(calculateProgress(data))
            if (data.harvestable.currentHarvestingProgress >= data.harvestSpeed) {
                supplies--
                data.onHarvest()
                data.harvestable.currentHarvestingProgress = 0.seconds
                progress(0f)
            }
            true
        } else {
            data.harvestable.currentHarvestingProgress = 0.seconds
            false
        }
    }
}