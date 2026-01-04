package org.roldy.rendering.g2d.gui

import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.gui.el.UIStage

interface UIContext {
    fun stage(): UIStage
    val disposable: AutoDisposable
}