package org.roldy.core.ui.el

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Align
import el.UIButton
import org.roldy.core.disposable.AutoDisposable
import org.roldy.core.ui.Scene2dDsl
import org.roldy.core.ui.TextActor
import org.roldy.core.ui.UIContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class UITextButton(
    initialText: String? = null,
    val style: KTextButtonStyle,
    val labelColor: Color,
    disposable: AutoDisposable
) : UIButton(disposable), UITableWidget, TextActor {
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
context(ctx: C)
fun <S, C : UIContext> UIWidget<S>.textButton(
    text: String? = null,
    font: BitmapFont,
    labelColor: Color = Color.WHITE,
    init: context(C) (@Scene2dDsl UITextButton).(S) -> Unit = {}
): UITextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UITextButton(text, textButtonStyle(font), labelColor, ctx.disposable), init)
}

fun textButtonStyle(
    font: BitmapFont
) =
    UITextButton.KTextButtonStyle(font)


