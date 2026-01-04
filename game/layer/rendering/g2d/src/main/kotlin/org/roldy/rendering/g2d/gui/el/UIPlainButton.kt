package org.roldy.rendering.g2d.gui.el

import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UIPlainButton(disposable: AutoDisposable) : UIButton(disposable), UITableWidget

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(ctx: C)
fun <S, C : UIContext> UIWidget<S>.plainButton(
    init: context(C) (@Scene2dDsl UIPlainButton).(S) -> Unit = {}
): UIPlainButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UIPlainButton(ctx.disposable), init)
}

