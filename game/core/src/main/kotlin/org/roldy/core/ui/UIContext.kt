package org.roldy.core.ui

import org.roldy.core.disposable.AutoDisposable
import org.roldy.core.ui.el.UIStage

interface UIContext {
    fun stage(): UIStage
    val disposable: AutoDisposable
}