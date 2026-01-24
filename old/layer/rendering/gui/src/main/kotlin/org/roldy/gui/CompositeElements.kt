package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.rendering.g2d.gui.DrawableDsl
import org.roldy.rendering.g2d.gui.drawable
import org.roldy.rendering.g2d.gui.redraw

@DrawableDsl
context(gui: GuiContext)
fun tooltipAnchor(
    transform: (x: Float, y: Float, width: Float, height: Float, (x: Float, y: Float, scaleX: Float, scaleY: Float) -> Unit) -> Unit
): Drawable {
    return drawable { x, y, width, height ->
        transform(x, y, width, height) { x, y, scaleX, scaleY ->
            val gem = gui.drawable(gui.colors.primary) { Tooltip_Anchor_Gem } redraw { x, y, w, h, draw ->
                draw(x + 6f*scaleX, y + 4f*scaleY, 40f * scaleX, 40f * scaleY)
            }

            val anchor = gui.drawable { Tooltip_Anchor } redraw { x, y, w, h, draw ->
                draw(x, y, 100f * scaleX, 100f * scaleY)
            }

            val offsetX = -2f
            anchor.draw(this, x + offsetX, y, width, height * scaleY)
            gem.draw(this, x + offsetX, y, width, height)
        }

    }

}