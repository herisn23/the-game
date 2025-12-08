package org.roldy.core.disposable

import com.badlogic.gdx.utils.Disposable

interface AutoDisposable : Disposable {
    val disposables: MutableList<Disposable>

    fun <T : Disposable?> T.disposable(): T {
        return also {
            if (it != null) {
                disposables.add(it)
            }
        }
    }
}

fun <T : Disposable?> AutoDisposable.disposable(initializer: () -> T): Lazy<T> {
    return lazy {
        initializer().disposable()
    }
}

fun <T : Disposable?> AutoDisposable.disposable(disposable: T): T {
    return disposable.also {
        if (it != null) {
            disposables.add(it)
        }
    }
}