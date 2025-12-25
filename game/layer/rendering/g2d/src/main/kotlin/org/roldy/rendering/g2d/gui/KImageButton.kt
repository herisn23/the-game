package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KImageButton(
    val drawable: KButtonOverlayDrawable
) : ImageButton(emptyImage(alpha(0f))), KTableWidget, KButton {
    val manager = KButtonManager(drawable, this)

    override fun draw(batch: Batch, parentAlpha: Float) {
        manager.draw(batch, parentAlpha)
        super.draw(batch, parentAlpha)
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.imageButton(
    drawable: KButtonOverlayDrawable,
    init: context(C) (@Scene2dDsl KImageButton).(S) -> Unit = {}
): KImageButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KImageButton(drawable), init)
}

