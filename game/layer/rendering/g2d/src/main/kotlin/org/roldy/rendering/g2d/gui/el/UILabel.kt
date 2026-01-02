package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import org.roldy.rendering.g2d.gui.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UILabel(
    initialText: String? = null,
     style: LabelStyle
) : Label(initialText, style), TextActor {
    private var textAction: (() -> String)? = null
    override fun updateText() {
        textAction?.let {
            val newText = it()
            if (!textEquals(newText)) {//update text only when its changed
                setText(newText)
            }
        }
    }

    fun setText(newText: () -> String) {
        textAction = newText
        updateText()
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.uiLabel(
    style: LabelStyle,
    init: context(C) (@Scene2dDsl UILabel).(S) -> Unit = {}
): UILabel {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UILabel(style = style), init)
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.uiLabel(
    text: String? = null,
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