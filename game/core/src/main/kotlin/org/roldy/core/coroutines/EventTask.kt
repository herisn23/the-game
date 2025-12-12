package org.roldy.core.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.roldy.core.logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class EventTask<D>(
    val delay: Duration = 16.milliseconds,
    val emitter: suspend () -> D,
) {
    private val logger by logger()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val dataChannel = Channel<D>(Channel.UNLIMITED)
    private val listeners = mutableListOf<(D) -> Unit>()
    private var active = true
    var paused = false

    fun start() {
        process()
        listen()
    }

    fun cancel() {
        active = false
        scope.cancel()
    }

    private fun listen() {
        scope.launch {
            // Consumer: actively receives and processes events
            for (event in dataChannel) {
                listeners.forEach {
                    it(event)
                }
            }
        }
    }

    fun addListener(listener: (D) -> Unit) =
        listeners.add(listener)

    private fun process() {
        scope.launch {
            while (active) {
                delay(delay)
                if (paused) {
                    continue
                }
                runCatching {
                    val data = emitter()
                    dataChannel.send(data)
                }.onFailure {
                    logger.error(it) { "Cannot emit data" }
                }
            }
        }
    }
}