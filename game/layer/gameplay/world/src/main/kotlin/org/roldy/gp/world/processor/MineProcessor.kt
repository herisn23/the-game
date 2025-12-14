package org.roldy.gp.world.processor

import org.roldy.core.coroutines.ConcurrentLoopConsumer
import org.roldy.data.state.GameState

class MineProcessor(
    val state: GameState
) : ConcurrentLoopConsumer<Float> {

    context(data: Float)
    override suspend fun update() {

    }

}