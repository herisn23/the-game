package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import org.roldy.rendering.g2d.gui.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UILabel(
    private var text: () -> String,
    style: LabelStyle
) : Label(text(), style), TextActor {
    override fun updateText() {
        val newText = text()
        if (!textEquals(newText)) {//update text only when its changed
            setText(newText)
        }
    }

    fun setText(newText: () -> String) {
        text = newText
        updateText()
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.label(
    text: () -> String,
    style: LabelStyle,
    init: context(C) (@Scene2dDsl UILabel).(S) -> Unit = {}
): UILabel {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UILabel(text, style), init)
}

@Scene2dCallbackDsl
fun UILabel.autoupdate() =
    addAction(updateTextAction())

fun labelStyle(init: LabelStyle.() -> Unit) =
    LabelStyle().also {
        init(it)
    }