package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.gui.KTextButton.KTextButtonStyle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Scene2dDsl
class KTextButton(
    val text: () -> String,
    val style: KTextButtonStyle
) : TextButton(text(), TextButtonStyle().apply {
    font = style.font
}), KTableWidget, TextActor {

    private var alpha: Float = 0f
    val tmp = Color()
    override fun updateText() {
        setText(text())
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        // Determine target
        val targetColor = when {
            isPressed -> style.transition.pressedColor
            isOver -> style.transition.overColor
            else -> style.transition.normalColor
        }

        // Animate
        alpha =
            MathUtils.lerp(
                alpha,
                targetColor.a,
                Gdx.graphics.deltaTime * style.transition.speed
            )
// Draw overlay
        if (this.color.a > 0.001f) {
            tmp.a = alpha * parentAlpha
            tmp.r = targetColor.r
            tmp.g = targetColor.g
            tmp.b = targetColor.b
            style.background.color = tmp
            style.background.draw(batch, x, y, width, height)
        }

        super.draw(batch, parentAlpha)
    }

    data class KTextButtonStyle(
        val font: BitmapFont,
        val background: KTextButtonOverlayDrawable,
        val transition: Transition = Transition()
    ) {
        data class Transition(
            val speed: Float = 8f,
            val normalColor: Color = Color.WHITE alpha 0f,
            val pressedColor: Color = Color.WHITE alpha .25f,
            val overColor: Color = Color.WHITE alpha .5f
        ) {
        }
    }
}

class KTextButtonOverlayDrawable(
    val drawable: Drawable,
) : BaseDrawable() {
    var color: Color = Color()

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        val masked = masked

        if (masked != null) {
            masked.color = color
        } else {
            batch.setColor(color.r, color.g, color.b, color.a)
        }

        drawable.draw(batch, x, y, width, height)

        batch.color = Color.WHITE
    }

    val masked by lazy {
        when (drawable) {
            is KMaskedImage -> drawable
            is KStackDrawable -> drawable.stack.filterIsInstance<KMaskedImage>().find {
                it.mask
            }

            else -> null
        }
    }

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

@Scene2dCallbackDsl
@OptIn(ExperimentalContracts::class)
fun KTextButton.onClick(onClick: () -> Unit) {
    addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            onClick()
        }
    })
}

@DrawableDsl
fun buttonStyle(
    font: BitmapFont,
    background: KTextButtonOverlayDrawable,
    transition: KTextButtonStyle.Transition = KTextButtonStyle.Transition()
) =
    KTextButtonStyle(font, background, transition)

@DrawableDsl
fun transition(
    speed: Float = 8f,
    normalColor: Color,
    pressedColor: Color,
    overColor: Color
) = KTextButtonStyle.Transition(speed, normalColor, pressedColor, overColor)

@DrawableDsl
fun buttonDrawable(drawable: () -> Drawable) =
    KTextButtonOverlayDrawable(drawable())
