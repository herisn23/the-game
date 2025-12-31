package org.roldy.gp.world.processor

import org.roldy.core.coroutines.ConcurrentLoopConsumer
import org.roldy.data.state.GameState
import org.roldy.gp.world.utils.Harvesting

class HarvestableRefreshingProcessor(
    val state: GameState
) : ConcurrentLoopConsumer<Float> {

    context(delta: Float)
    override suspend fun update() {
        collectAllRefreshing().forEach {
            Harvesting.refresh(it)
        }
    }

    private fun collectAllRefreshing() =
        state.mines.map { it.refreshing }

}