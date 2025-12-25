package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.KTextButton.KTextButtonStyle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KTextButton(
    text: () -> String,
    val style: KTextButtonStyle
) : Button(ButtonStyle().apply {
    up = emptyImage(alpha(0f))
}), KTableWidget, TextActor, KButton {
    val label = KLabel(text, labelStyle {
        font = style.font
    })
    private val manager = KButtonManager(style.background, this)

    init {
        label.setAlignment(Align.center)
        add(label).grow()
    }

    override fun updateText() {
        label.updateText()

    }
    override fun draw(batch: Batch, parentAlpha: Float) {
        manager.draw(batch, parentAlpha)
        super.draw(batch, parentAlpha)
    }


    data class KTextButtonStyle(
        val font: BitmapFont,
        val background: KButtonOverlayDrawable,

        )
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.textButton(
    text: () -> String,
    style: KTextButtonStyle,
    init: context(C) (@Scene2dDsl KTextButton).(S) -> Unit = {}
): KTextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KTextButton(text, style), init)
}
@DrawableDsl
fun textButtonStyle(
    font: BitmapFont,
    background: KButtonOverlayDrawable
) =
    KTextButtonStyle(font, background)


