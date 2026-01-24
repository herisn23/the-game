package org.roldy.core.disposable

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.utils.Disposable

abstract class AutoDisposableApplicationAdapter : ApplicationAdapter(), AutoDisposable {
    override val disposables: MutableList<Disposable> = mutableListOf()
    override fun dispose() {
        disposables.forEach(Disposable::dispose)
    }
}