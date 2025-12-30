package org.roldy.rendering.g2d.gui.el

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
    text: () -> String,
    val style: KTextButtonStyle
) : UIButton(), UITableWidget, TextActor {
    val label = UILabel(text, labelStyle {
        font = style.font
    })

    init {
        label.setAlignment(Align.center)
        add(label).grow()
    }

    override fun updateText() {
        label.updateText()

    }

    data class KTextButtonStyle(
        val font: BitmapFont
    )
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.textButton(
    text: () -> String,
    font: BitmapFont,
    init: context(C) (@Scene2dDsl UITextButton).(S) -> Unit = {}
): UITextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UITextButton(text, textButtonStyle(font)), init)
}

fun textButtonStyle(
    font: BitmapFont
) =
    KTextButtonStyle(font)


