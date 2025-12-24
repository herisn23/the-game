package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import sun.font.TextLabel
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KLabel(
    val text: () -> String,
    style: LabelStyle
) : Label(text(), style), TextActor {
    override fun updateText() {
        setText(text())
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.label(
    text: () -> String,
    style: LabelStyle,
    init: context(C) (@Scene2dDsl KLabel).(S) -> Unit = {}
): KLabel {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KLabel(text, style), init)
}