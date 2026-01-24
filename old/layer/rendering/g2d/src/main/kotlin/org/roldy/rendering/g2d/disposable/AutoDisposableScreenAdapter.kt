package org.roldy.rendering.g2d.disposable

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.utils.Disposable

abstract class AutoDisposableScreenAdapter : ScreenAdapter(), AutoDisposable {
    override val disposables: MutableList<Disposable> = mutableListOf()
    override fun dispose() {
        disposables.forEach(Disposable::dispose)
    }
}