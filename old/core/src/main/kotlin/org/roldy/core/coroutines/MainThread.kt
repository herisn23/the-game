package org.roldy.core.coroutines

import com.badlogic.gdx.Gdx
import java.util.concurrent.CountDownLatch

@JvmName("onMainThread")
fun onGPUThread(onRenderThread: () -> Unit) {
    Gdx.app.postRunnable(onRenderThread)
}

@JvmName("onMainThreadLambda")
fun <D> onGPUThread(onRenderThread: (D) -> Unit): (D) -> Unit = { data ->
    onGPUThread {
        onRenderThread(data)
    }
}

fun <A : Any> onGPUThreadBlocking(load: () -> A): A {
    lateinit var data: A
    val latch = CountDownLatch(1)

    onGPUThread {
        data = load()
        latch.countDown()
    }

    latch.await() // Blocks efficiently without spinning
    return data
}