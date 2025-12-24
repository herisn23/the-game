package org.roldy.core.coroutines

import org.roldy.core.TimeManager

class DeltaProcessingLoop(
    val timeManager: TimeManager
) {
    private val task = ConcurrentLoop(emitter = ::calculateDelta)

    private var lastLoopTime = -1L
    private var deltaTime = 0f
    var resetDeltaTime = false
    val delta get() = deltaTime

    private fun calculateDelta(): Float {
        val time = System.nanoTime()
        if (lastLoopTime == -1L) lastLoopTime = time
        if (resetDeltaTime) {
            resetDeltaTime = false
            deltaTime = 0f
        } else {
            deltaTime = (time - lastLoopTime) / 1000000000.0f
        }
        lastLoopTime = time

        return timeManager.getDelta(deltaTime)
    }

    fun addConsumer(consumer: ConcurrentLoopConsumer<Float>) {
        task.addConsumer(consumer)
    }

    fun cancel() {
        task.cancel()
    }

}