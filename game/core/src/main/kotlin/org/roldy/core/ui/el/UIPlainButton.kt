package org.roldy.core.ui.el

import el.UIButton
import org.roldy.core.disposable.AutoDisposable
import org.roldy.core.ui.Scene2dDsl
import org.roldy.core.ui.UIContext
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

