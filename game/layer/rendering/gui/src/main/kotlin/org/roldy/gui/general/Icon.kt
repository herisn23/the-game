package org.roldy.gui.general

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.gui.iconBackground
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIImage
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.image
import org.roldy.rendering.g2d.gui.el.table

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.icon(
    icon: Drawable = gui.pixmap(alpha(0f)),
    init: (@Scene2dDsl UIImage).(S) -> Unit = {}
) = table(true) { storage ->
    image(iconBackground { this })
    table {
        pad(20f)
        image(icon) {
            it.width(136f).height(136f).center()
            init(storage)
        }
    }
}