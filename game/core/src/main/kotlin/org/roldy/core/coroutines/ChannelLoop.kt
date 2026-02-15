package org.roldy.core.coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun interface ChannelLoopConsumer<D> {
    context(data: D)
    fun update()
}

class ChannelLoop<D>(
    val emitter: suspend () -> D,
    val delayDuration: Duration = 100.milliseconds
) : Loop() {
    private val dataChannel = Channel<D>(Channel.UNLIMITED)
    private val consumers = mutableListOf<ChannelLoopConsumer<D>>()

    override fun start() {
        start {
            runCatching {
                val data = emitter()
                dataChannel.send(data)
            }.onFailure {
                logger.error("Error in event loop", it)
            }
        }
        listen()
    }

    private fun listen() {
        scope.launch {
            delay(delayDuration)
            // Consumer: actively receives and processes events
            for (event in dataChannel) {
                synchronized(consumers) {
                    consumers.forEach {
                        context(event) {
                            it.update()
                        }
                    }
                }
            }
        }
    }

    fun addConsumer(listener: ChannelLoopConsumer<D>) =
        synchronized(consumers) {
            consumers.add(listener)
        }

    fun addConsumer(listener: (D) -> Unit) =
        synchronized(consumers) {
            consumers.add(listener)
        }
}