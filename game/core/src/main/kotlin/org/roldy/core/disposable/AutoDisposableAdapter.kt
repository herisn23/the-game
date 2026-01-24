package org.roldy.core.disposable

import com.badlogic.gdx.utils.Disposable

abstract class AutoDisposableAdapter: AutoDisposable {
    override val disposables: MutableList<Disposable> = mutableListOf()
}