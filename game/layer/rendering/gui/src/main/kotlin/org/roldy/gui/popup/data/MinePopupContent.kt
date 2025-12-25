package org.roldy.gui.popup.data

import com.badlogic.gdx.utils.Align
import org.roldy.core.i18n.t
import org.roldy.data.state.MineState
import org.roldy.gui.GuiContext
import org.roldy.gui.button.smallButton
import org.roldy.gui.label
import org.roldy.rendering.g2d.gui.KPopup
import org.roldy.rendering.g2d.gui.KTable
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.i18n.localizable
import org.roldy.rendering.g2d.gui.onClick

@Scene2dCallbackDsl
context(guiContext: GuiContext)
fun KTable.minePopupContent(popup: KPopup, mine: MineState) {
    align(Align.left)

    label({ mine.harvestable.name }, 35, false)

    row()

    localizable(t { mine_supplies.format(mine.refreshing.current) }) {
        label(it)
    }

    row()

    localizable(t { mine_supplies.format(mine.refreshing.max) }) {
        label(it)
    }

    row()

    smallButton(t{close}) {
        onClick {
            popup.hide()
        }
    }

}