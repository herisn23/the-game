package org.roldy.gui

import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.sequencer
import org.roldy.gui.button.mainButton
import org.roldy.gui.button.smallButton
import org.roldy.rendering.g2d.gui.KWidget
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.onClick
import org.roldy.rendering.g2d.gui.table
import kotlin.contracts.ExperimentalContracts
import kotlin.system.exitProcess

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.example() {

    val langs by sequencer { gui.i18n.languages }
//    image(emptyImage(Color.GRAY)) {
//        setSize(stage.width, stage.height)
//    }
    val playerInventory = mutableListOf(
        "1", "2", "3"
    )
    val window = inventory {

        onSlotClick {
            println("SlotClicked")
        }
    }

    table {
        align(Align.top)
        setFillParent(true)

        mainButton(string { "Open window" }) {
            onClick {
                window.open()
            }
        }
//        squareButton(gui.drawable { Ico_Plus })
//        circularButton(gui.drawable { Ico_Plus })
//        circularButton(gui.drawable { Ico_Plus }, CircularButtonSize.S)
//        circularButton(gui.drawable { Ico_Plus }, CircularButtonSize.L)
        smallButton(translate { close }) {
            onClick {
                exitProcess(1)
            }
        }
        row()

    }
}