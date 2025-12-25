package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.sequencer
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.KWidget
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.image
import org.roldy.rendering.g2d.gui.table
import kotlin.contracts.ExperimentalContracts

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
        guiWindow({ "overlay" }) {
            it.padTop(100f).padRight(20f)
//            mainButton(t{lang})
        }
        val window = guiWindow({ "Title" }) {
            it.padTop(100f)
//            mainButton(t{lang})
        }
//        mainButton(t { lang }) {
//            onClick {
////                gui.i18n.locale = langs.next()
//                window.open()
//            }
//        }
//        squareButton(gui.drawable { Plus })
//        circularButton(gui.drawable { Plus })
//        circularButton(gui.drawable { Plus }, CircularButtonSize.S)
//        circularButton(gui.drawable { Plus }, CircularButtonSize.L)
//        smallButton(t { close }) {
//            onClick {
//                exitProcess(1)
//            }
//        }
//        row()

    }
}