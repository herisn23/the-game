package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KImageButton(
    val drawable: KButtonOverlayDrawable,
    val disabled: Drawable
) : ImageButton(
    ImageButtonStyle().apply {
        imageUp = emptyImage(alpha(0f))
        imageDisabled = disabled
    }
), KTableWidget, KButton {
    val normalState = KButtonDrawableManager(drawable, this)
    override fun draw(batch: Batch, parentAlpha: Float) {
        if (this.isDisabled) {
            disabled.draw(batch, x, y, width, height)
        } else {
            normalState.draw(batch, parentAlpha)
        }
        super.draw(batch, parentAlpha)
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.imageButton(
    drawable: KButtonOverlayDrawable,
    disabled: Drawable = emptyImage(alpha(0f)),
    init: context(C) (@Scene2dDsl KImageButton).(S) -> Unit = {}
): KImageButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KImageButton(drawable, disabled), init)
}

