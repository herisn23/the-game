package org.roldy.gui.popup.data

import com.badlogic.gdx.utils.Align
import org.roldy.data.state.MineState
import org.roldy.gui.GuiContext
import org.roldy.gui.button.smallButton
import org.roldy.gui.label
import org.roldy.gui.string
import org.roldy.gui.translate
import org.roldy.rendering.g2d.gui.KPopup
import org.roldy.rendering.g2d.gui.KTable
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.onClick

@Scene2dCallbackDsl
context(guiContext: GuiContext)
fun KTable.minePopupContent(popup: KPopup, mine: MineState) {
    align(Align.left)

    label(string { mine.harvestable.name }, 35)

    row()

    label(translate { mine_supplies.format(mine.refreshing.current) })

    row()

    label(translate { mine_supplies.format(mine.refreshing.max) })

    row()

    smallButton(translate { close }) {
        onClick {
            popup.hide()
        }
    }

}