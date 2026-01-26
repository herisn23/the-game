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

    override fun dispose() {
        disposables.forEach(Disposable::dispose)
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

fun <T : Disposable?> AutoDisposable.disposableList(initializer: () -> List<T>): Lazy<List<T>> {
    return lazy {
        disposableList(initializer())
    }
}

fun <T : Disposable?> AutoDisposable.disposableList(
    vararg elements: T
) = disposableList(elements.asList())

fun <T : Disposable?> AutoDisposable.disposableList(
    elements: List<T>
) = elements.map {
    it.disposable()
}