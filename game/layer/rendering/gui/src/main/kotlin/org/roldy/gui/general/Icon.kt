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
import org.roldy.rendering.g2d.pixmap

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.icon(
    icon: Drawable = pixmap(alpha(0f)),
    init: (@Scene2dDsl UIImage).(S) -> Unit = {}
) = table(true) { storage ->
    image(iconBackground { this })
    table {
        image(icon) {
            it.pad(20f)
            init(storage)
        }
    }
}