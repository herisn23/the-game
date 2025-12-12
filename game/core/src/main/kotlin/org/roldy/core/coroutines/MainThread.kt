package org.roldy.core.coroutines

import com.badlogic.gdx.Gdx

@JvmName("onMainThread")
fun onGPUThread(onRenderThread: () -> Unit) {
    Gdx.app.postRunnable(onRenderThread)
}

@JvmName("onMainThreadLambda")
fun <D> onGPUThread(onRenderThread: (D) -> Unit): (D)-> Unit = { data->
    onGPUThread {
        onRenderThread(data)
    }
}