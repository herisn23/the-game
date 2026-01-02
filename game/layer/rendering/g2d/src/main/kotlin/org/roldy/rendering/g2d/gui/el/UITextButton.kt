package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Align
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.TextActor
import org.roldy.rendering.g2d.gui.UIContext
import org.roldy.rendering.g2d.gui.el.UITextButton.KTextButtonStyle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class UITextButton(
    initialText: String? = null,
    val style: KTextButtonStyle,
    val labelColor: Color
) : UIButton(), UITableWidget, TextActor {
    val label = UILabel(initialText, labelStyle {
        font = style.font
    }).apply {
        color = this@UITextButton.labelColor
    }

    init {
        label.setAlignment(Align.center)
        add(label).grow()
    }

    override fun updateText() {
        label.updateText()
    }

    fun setText(text: String?) {
        label.setText(text)
    }

    fun setText(text: () -> String) {
        label.setText(text)
    }

    data class KTextButtonStyle(
        val font: BitmapFont
    )
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.textButton(
    text: String? = null,
    font: BitmapFont,
    labelColor: Color = Color.WHITE,
    init: context(C) (@Scene2dDsl UITextButton).(S) -> Unit = {}
): UITextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UITextButton(text, textButtonStyle(font), labelColor), init)
}

fun textButtonStyle(
    font: BitmapFont
) =
    KTextButtonStyle(font)


