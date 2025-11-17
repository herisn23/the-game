package org.roldy.coroutines

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
fun async(action: Coroutine) {
    scope.launch {
        action { onRenderThread ->
            // Post the runnable to the LibGDX application loop,
            // ensuring it runs on the rendering thread.
            Gdx.app.postRunnable(onRenderThread)
        }
    }
}