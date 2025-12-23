package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KTextButton(
    override val text: () -> String,
    style: TextButtonStyle
) : TextButton(text(), style), KTableWidget, Text {
    override fun updateText() {
        setText(text())
    }
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.textButton(
    text: () -> String,
    style: TextButton.TextButtonStyle,
    init: context(C) (@Scene2dDsl KTextButton).(S) -> Unit
): KTextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KTextButton(text, style), init)
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun KTextButton.onClick(onClick: () -> Unit) {
    addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            onClick()
        }
    })
}
fun buttonStyle(init: TextButton.TextButtonStyle.() -> Unit): TextButton.TextButtonStyle =
    TextButton.TextButtonStyle().apply {
        init()
    }
