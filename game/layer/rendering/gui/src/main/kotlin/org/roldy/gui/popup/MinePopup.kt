package org.roldy.gui.popup

import com.badlogic.gdx.utils.Align
import org.roldy.core.i18n.t
import org.roldy.data.state.MineState
import org.roldy.gui.GuiContext
import org.roldy.gui.label
import org.roldy.gui.simpleButton
import org.roldy.rendering.g2d.gui.KPopup
import org.roldy.rendering.g2d.gui.KTable
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.i18n.localizable
import org.roldy.rendering.g2d.gui.onClick

@Scene2dCallbackDsl
context(guiContext: GuiContext)
fun KTable.minePopup(popup: KPopup, mine: MineState) {
    align(Align.left)

    label({ "mine.harvestable.name" }, 35)

    row()

    localizable(t { mine_supplies.format(mine.refreshing.current) }) {
        label(it)
    }

    row()

    localizable(t { mine_supplies.format(mine.refreshing.max) }) {
        label(it)
    }

    row()
    simpleButton(t{close}) {
        onClick {
            println("CLosing popup")
            popup.hide()
        }
    }

}