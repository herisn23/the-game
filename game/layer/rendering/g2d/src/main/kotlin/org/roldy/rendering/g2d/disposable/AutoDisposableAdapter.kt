package org.roldy.rendering.g2d.disposable

import com.badlogic.gdx.utils.Disposable

abstract class AutoDisposableAdapter: AutoDisposable {
    override val disposables: MutableList<Disposable> = mutableListOf()
}