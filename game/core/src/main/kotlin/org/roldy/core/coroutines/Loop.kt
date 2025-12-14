package org.roldy.core.coroutines

import kotlinx.coroutines.*
import org.roldy.core.logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

abstract class Loop(
    val delay: Duration = 16.milliseconds
) {
    protected val logger by logger()
    private val scope = CoroutineScope(Dispatchers.Default)
    private var active = true
    var paused = false

    fun cancel() {
        active = false
        scope.cancel()
    }

    protected abstract fun start()

    init {
        start()
    }

    protected fun start(process: suspend () -> Unit) {
        scope.launch {
            while (active) {
                delay(delay)
                if (paused) {
                    continue
                }
                process()
            }
        }
    }
}