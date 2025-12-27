package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.anim.AnimationDrawableState
import org.roldy.rendering.g2d.gui.anim.AnimationDrawableStateResolver
import org.roldy.rendering.g2d.gui.anim.Disabled
import org.roldy.rendering.g2d.gui.DrawableDsl
import org.roldy.rendering.g2d.gui.anim.AlphaAnimationDrawable
import org.roldy.rendering.g2d.gui.anim.Normal
import org.roldy.rendering.g2d.gui.anim.Over
import org.roldy.rendering.g2d.gui.anim.Pressed
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.TextActor
import org.roldy.rendering.g2d.gui.UIContext
import org.roldy.rendering.g2d.gui.anim.alpha
import org.roldy.rendering.g2d.gui.anim.delta
import org.roldy.rendering.g2d.gui.el.UITextButton.KTextButtonStyle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UITextButton(
    text: () -> String,
    val style: KTextButtonStyle
) : Button(ButtonStyle().apply {
    up = emptyImage(alpha(0f))
}), UITableWidget, TextActor, UIButton, AnimationDrawableStateResolver {
    val label = UILabel(text, labelStyle {
        font = style.font
    })
    private val drawable = alpha(style.background, style.transition).delta()

    init {
        label.setAlignment(Align.center)
        add(label).grow()
    }

    override fun updateText() {
        label.updateText()

    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawable.draw(batch, x, y, width, height)
        super.draw(batch, parentAlpha)
    }

    override val state: AnimationDrawableState
        get() = when {
            isOver -> Over
            isPressed -> Pressed
            isDisabled -> Disabled
            else -> Normal
        }


    data class KTextButtonStyle(
        val font: BitmapFont,
        val background: Drawable,
        val transition: AlphaAnimationDrawable.Transition
    )
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.textButton(
    text: () -> String,
    style: KTextButtonStyle,
    init: context(C) (@Scene2dDsl UITextButton).(S) -> Unit = {}
): UITextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UITextButton(text, style), init)
}

@DrawableDsl
fun textButtonStyle(
    font: BitmapFont,
    background: Drawable,
    transition: AlphaAnimationDrawable.Transition
) =
    KTextButtonStyle(font, background, transition)


