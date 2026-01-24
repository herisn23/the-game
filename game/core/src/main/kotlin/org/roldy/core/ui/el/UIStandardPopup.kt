package org.roldy.core.ui.el

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.ui.Scene2dDsl
import org.roldy.core.ui.UIContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UIStandardPopup(
    background: Drawable,
    private val anchor: Image
) : UIPopup() {

    init {
        this.background = background
    }

    var anchorOffsetX = 0f
    var anchorOffsetY = 0f

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        anchor.x = this.x + width * .5f + anchorOffsetX
        anchor.y = this.y + anchorOffsetY

        anchor.draw(batch, parentAlpha)
    }

    override fun updatePosition() {
        val vec = calculatePosition()
        setPosition(vec.x - width / 2, vec.y)
    }
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <C : UIContext, S> UIWidget<S>.standardPopup(
    background: Drawable,
    anchor: Image,
    init: context(C) (@Scene2dDsl UIStandardPopup).(S) -> Unit = {}
): UIStandardPopup {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UIStandardPopup(background, anchor), init)
}