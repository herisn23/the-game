package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.sequencer
import org.roldy.gui.button.*
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import kotlin.contracts.ExperimentalContracts
import kotlin.system.exitProcess

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.example() {

    val langs by sequencer { gui.i18n.languages }
    image(emptyImage(Color.BLACK)) {
        setSize(stage.width, stage.height)
    }
    table {
        align(Align.top)
        setFillParent(true)
        val window = guiWindow(string { "Title" }) {
            it.padTop(100f)
        }
        mainButton(string { "Close" }) {
            onClick {
                window.open()
            }
        }
        squareButton(gui.drawable { Plus })
        circularButton(gui.drawable { Plus })
        circularButton(gui.drawable { Plus }, CircularButtonSize.S)
        circularButton(gui.drawable { Plus }, CircularButtonSize.L)
        smallButton(translate { close }) {
            onClick {
                exitProcess(1)
            }
        }
        row()

    }
}