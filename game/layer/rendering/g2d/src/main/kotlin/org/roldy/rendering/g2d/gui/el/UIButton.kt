package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.anim.core.AnimatedDrawable
import org.roldy.rendering.g2d.gui.anim.core.AnimationDrawableStateResolver
import org.roldy.rendering.g2d.gui.anim.core.delta
import org.roldy.rendering.g2d.pixmap
import kotlin.contracts.ExperimentalContracts

abstract class UIButton(disposable: AutoDisposable) : Button(ButtonStyle().apply { up = disposable.pixmap(alpha(0f)) }),
    AnimationDrawableStateResolver<UIAnimationState> {
    val graphics = ButtonDrawable(this)
    private var drawable: Drawable = disposable.pixmap(Color.WHITE)

    class ButtonDrawable(
        private val button: UIButton
    ) {
        operator fun invoke(drawable: () -> Drawable) {
            button.drawable = drawable().run {
                when (this) {
                    is AnimatedDrawable -> delta()
                    else -> this
                }
            }
        }
    }

    override fun getBackgroundDrawable(): Drawable {
        return drawable
    }

    override val state: UIAnimationState
        get() = when {
            isDisabled -> Disabled
            isPressed -> Pressed
            isOver -> Over
            else -> Normal
        }
}


@Scene2dCallbackDsl
@OptIn(ExperimentalContracts::class)
fun UIButton.onClick(button: Int = Input.Buttons.LEFT, onClick: (InputEvent) -> Unit) {
    addListener(object : ClickListener(button) {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            if (state != Disabled)
                onClick(event)
        }
    })
}
