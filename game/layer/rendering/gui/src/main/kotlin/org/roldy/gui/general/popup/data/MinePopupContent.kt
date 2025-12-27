package org.roldy.gui.general.popup.data

import com.badlogic.gdx.utils.Align
import org.roldy.data.state.MineState
import org.roldy.gui.GuiContext
import org.roldy.gui.general.button.smallButton
import org.roldy.gui.general.label
import org.roldy.gui.string
import org.roldy.gui.translate
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.el.UIPopup
import org.roldy.rendering.g2d.gui.el.UITable
import org.roldy.rendering.g2d.gui.el.onClick

@Scene2dCallbackDsl
context(guiContext: GuiContext)
fun UITable.minePopupContent(popup: UIPopup, mine: MineState) {
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