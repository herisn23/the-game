package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.anim.AnimationDrawableState
import org.roldy.rendering.g2d.gui.anim.AnimationDrawableStateResolver
import org.roldy.rendering.g2d.gui.anim.Disabled
import org.roldy.rendering.g2d.gui.anim.AlphaAnimationDrawable
import org.roldy.rendering.g2d.gui.anim.Normal
import org.roldy.rendering.g2d.gui.anim.Over
import org.roldy.rendering.g2d.gui.anim.Pressed
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext
import org.roldy.rendering.g2d.gui.anim.alpha
import org.roldy.rendering.g2d.gui.anim.delta
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UIImageButton(
    val drawable: Drawable,
    val disabledDrawable: Drawable,
    transition: AlphaAnimationDrawable.Transition
) : ImageButton(
    ImageButtonStyle().apply {
        imageUp = emptyImage(alpha(0f))
    }
), UITableWidget, UIButton, AnimationDrawableStateResolver {
    val normalState = alpha(drawable, transition).delta()
    override fun draw(batch: Batch, parentAlpha: Float) {
        if (this.isDisabled) {
            disabledDrawable.draw(batch, x, y, width, height)
        } else {
            normalState.draw(batch, x, y, width, height)
        }
        super.draw(batch, parentAlpha)
    }

    override val state: AnimationDrawableState
        get() = when {
            isOver -> Over
            isPressed -> Pressed
            isDisabled -> Disabled
            else -> Normal
        }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.imageButton(
    drawable: Drawable,
    disabled: Drawable = emptyImage(alpha(0f)),
    transition: AlphaAnimationDrawable.Transition,
    init: context(C) (@Scene2dDsl UIImageButton).(S) -> Unit = {}
): UIImageButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UIImageButton(drawable, disabled, transition), init)
}

