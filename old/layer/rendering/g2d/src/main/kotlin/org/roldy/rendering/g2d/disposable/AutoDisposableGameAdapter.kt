package org.roldy.rendering.g2d.disposable

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.Disposable

abstract class AutoDisposableGameAdapter : Game(), AutoDisposable {
    override val disposables: MutableList<Disposable> = mutableListOf()
    override fun dispose() {
        disposables.forEach(Disposable::dispose)
    }
}