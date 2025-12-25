package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KStandardPopup(
    background: Drawable,
    private val anchor: Image
) : KPopup() {

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
fun <C : KContext, S> KWidget<S>.standardPopup(
    background: Drawable,
    anchor: Image,
    init: context(C) (@Scene2dDsl KStandardPopup).(S) -> Unit = {}
): KStandardPopup {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KStandardPopup(background, anchor), init)
}