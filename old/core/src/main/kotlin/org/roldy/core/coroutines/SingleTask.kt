package org.roldy.core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Represents a function that ensures execution
 * on the GPU rendering thread. Use this when interacting
 * with the graphics context to maintain thread safety.
 */
typealias OnRenderThread = (() -> Unit) -> Unit

/**
 * Defines a suspendable function that accepts
 * a [OnRenderThread] callback. This allows asynchronous work
 * without blocking the rendering thread.
 */
typealias Coroutine = suspend (OnRenderThread) -> Unit

// Coroutine scope for launching background tasks.
// Uses the Default dispatcher for parallel execution.
val scope = CoroutineScope(Dispatchers.Default)

/**
 * Launches a coroutine and provides a safe way to
 * synchronize back to the GPU rendering thread.
 *
 * @param action The suspendable coroutine logic that
 *               can request GPU-thread execution via [SyncToGPU].
 *
 * ### Example
 * ```kotlin
 * async { onRender ->
 *     val title = httpClient.getTitle()
 *     onRender {
 *         Gdx.graphics.setTitle(title)
 *     }
 * }
 * ```
 */
fun async(action: Coroutine): Job =
    scope.launch {
        action(::onGPUThread)
    }

class SingleTaskAsync {
    var lastJob: Job? = null
    operator fun invoke(action: Coroutine) {
        lastJob?.cancel()
        lastJob = async {
            action(it)
            lastJob = null
        }
    }

    fun cancel() {
        lastJob?.cancel()
        lastJob = null
    }

    val isActive: Boolean
        get() = lastJob?.isActive == true
}

fun singleTask() = lazy { SingleTaskAsync() }