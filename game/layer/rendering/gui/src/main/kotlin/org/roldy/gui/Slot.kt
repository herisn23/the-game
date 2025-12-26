package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import slotHover

const val SlotSize = 126f

@Scene2dCallbackDsl
data class Slot(
    private val button: KImageButton,
    private val icon: KImage,
    private val additionalContent: KTable
) {

    fun onClick(onClick: () -> Unit) {
        button.onClick(onClick)
    }

    fun setIcon(drawable: Drawable) {
        icon.drawable = drawable
    }

    fun content(cnt: KTable.() -> Unit) {
        additionalContent.cnt()
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.slot(
    init: (@Scene2dDsl Slot).() -> Unit = {}
): KTable =
    table(true) {
        image(gui.region { Slot_Background })
        lateinit var icon: KImage
        lateinit var content: KTable
        table(true) {
            pad(8f)
            image(emptyImage(alpha(0f))) {
                icon = this
            }
        }
        table {
            pad(8f)
            content = this
        }
        table(true) {
            pad(-9f)
            image(gui.region { Slot_Border }) {
                touchable = Touchable.disabled
            }
        }
        table(true) {
            pad(-3f)

            imageButton(
                buttonDrawable(
                    drawable = slotHover { this },
                    transition = transition(
                        normalColor = Color.WHITE alpha 0f,
                        pressedColor = Color.WHITE alpha .4f,
                        overColor = Color.WHITE alpha 1f
                    )
                )
            ) {
                Slot(this, icon, content).init()
            }
        }
    }