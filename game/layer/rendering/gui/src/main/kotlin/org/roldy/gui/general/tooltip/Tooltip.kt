package org.roldy.gui.general.tooltip

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.gui.generalContainerBorder2
import org.roldy.gui.tooltipAnchor
import org.roldy.gui.tooltipBackground
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIContextualTooltip
import org.roldy.rendering.g2d.gui.el.UIContextualTooltipContent
import org.roldy.rendering.g2d.gui.el.contextualTooltip
import org.roldy.rendering.g2d.gui.redraw
import org.roldy.rendering.g2d.pixmap


/**
 * Sub tooltip
 */
@Scene2dDsl
context(gui: GuiContext)
fun UIContextualTooltipContent.tooltip(
    actor: Actor,
    init: (@Scene2dDsl UIContextualTooltip).() -> Unit = {}
): UIContextualTooltip =
    actor.tooltip(root, init)


/**
 * Root tooltip
 */
@Scene2dDsl
context(gui: GuiContext)
fun Actor.tooltip(
    parent: UIContextualTooltip? = null,
    init: (@Scene2dDsl UIContextualTooltip).() -> Unit = {}
): UIContextualTooltip {
    var adjustedX: Float
    var adjustedY: Float
    var scaleX: Float
    var scaleY: Float
    fun anchor(anchorPosition: UIContextualTooltip.AnchorPosition) =
        tooltipAnchor { x, y, w, h, draw ->
            when (anchorPosition) {
                UIContextualTooltip.AnchorPosition.TOP_LEFT -> {
                    adjustedX = x
                    adjustedY = y + h
                    scaleX = 1f
                    scaleY = -1f
                }

                UIContextualTooltip.AnchorPosition.TOP_RIGHT -> {
                    adjustedX = x + w
                    adjustedY = y + h
                    scaleX = -1f
                    scaleY = -1f
                }

                UIContextualTooltip.AnchorPosition.BOTTOM_LEFT -> {
                    adjustedX = x
                    adjustedY = y
                    scaleX = 1f
                    scaleY = 1f
                }

                UIContextualTooltip.AnchorPosition.BOTTOM_RIGHT -> {
                    adjustedX = x + w
                    adjustedY = y
                    scaleX = -1f
                    scaleY = 1f
                }
            }
            draw(adjustedX, adjustedY, scaleX, scaleY)
        }

    val background = tooltipBackground { this }
    val pinBorder = generalContainerBorder2 {
        apply {
            setPadding(-10f)
        }
    }
    val padding = 30f
    val pinProgress = pixmap(Color.BLACK alpha .1f) redraw { x, y, width, height, draw ->
        draw(x + padding, y + padding, width - padding * 2, height - padding * 2)
    }
    return contextualTooltip(
        UIContextualTooltip.Drawables(
            background,
            pinBorder,
            anchor(UIContextualTooltip.AnchorPosition.TOP_LEFT),
            anchor(UIContextualTooltip.AnchorPosition.TOP_RIGHT),
            anchor(UIContextualTooltip.AnchorPosition.BOTTOM_LEFT),
            anchor(UIContextualTooltip.AnchorPosition.BOTTOM_RIGHT),
            pinProgress
        )
    ) {
        parent?.nestedTooltip = this
        pad(60f, 40f, 60f, 40f)
        minWidth(128f)
        minHeight(128f)
        addListener(this)
        init()
    }
}

