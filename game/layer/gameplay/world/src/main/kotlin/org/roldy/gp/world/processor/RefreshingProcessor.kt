package org.roldy.gp.world.processor

import org.roldy.core.coroutines.ConcurrentLoopConsumer
import org.roldy.data.state.GameState
import org.roldy.data.state.MineState
import org.roldy.data.state.RefreshingState
import kotlin.time.Duration.Companion.seconds

class RefreshingProcessor(
    val state: GameState
) : ConcurrentLoopConsumer<Float> {

    context(delta: Float)
    override suspend fun update() {
        collectAllRefreshing().forEach {
            it.refresh()
        }
    }

    context(delta: Float)
    private fun RefreshingState.refresh() {
        if (current < max) {
            // Accumulate time (data is delta time in seconds)
            currentRefreshTime += delta.toDouble().seconds

            // Refresh while we have enough time and haven't reached max
            while (currentRefreshTime >= timeToRefresh && current < max) {
                current++
                currentRefreshTime -= timeToRefresh
            }
        } else {
            // Reset refresh timer when at max capacity
            currentRefreshTime = 0.seconds
        }
    }

    private fun collectAllRefreshing() =
        state.mines.map { it.refreshing }

}