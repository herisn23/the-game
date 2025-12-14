package org.roldy.core.coroutines


fun interface ConcurrentLoopConsumer<D> {
    context(data: D)
    suspend fun update()
}

class ConcurrentLoop<D>(
    val emitter: suspend () -> D,
) : Loop() {
    private val listeners = mutableListOf<ConcurrentLoopConsumer<D>>()

    override fun start() {
        start {
            runCatching {
                val data = emitter()
                val snapshot = synchronized(listeners) { listeners.toList() }
                snapshot.forEach {
                    context(data) {
                        it.update()
                    }
                }
            }.onFailure {
                logger.error("Error in concurrent loop", it)
            }
        }
    }

    fun addListener(consumer: ConcurrentLoopConsumer<D>) =
        synchronized(listeners) {
            listeners.add(consumer)
        }
}