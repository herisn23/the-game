package org.roldy.gui

import com.badlogic.gdx.utils.Align
import org.roldy.gui.general.button.*
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.onClick
import org.roldy.rendering.g2d.gui.el.table
import kotlin.system.exitProcess

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.example() {


//    image(emptyImage(Color.BLACK)) {
//        setFillParent(true)
//    }

    table {
        this.pad(20f)
        name = "ExampleTable"
        align(Align.top)
        setFillParent(true)


        mainButton(string { "Close window and do something more" }) {
            name = "Close window"
//            isDisabled = true
            onClick {
                isDisabled = true

            }
        }
        mainButton(string { "ěščřžýáíé" }) {
            onClick {

            }
        }
        squareButton(gui.drawable { Ico_Plus }) {
            onClick {

            }
        }
        squareButton(gui.drawable { Ico_Menu }) {
            isDisabled = true
        }
        circularButton(gui.drawable { Ico_Plus }, CircularButtonSize.S)
        circularButton(gui.drawable { Ico_Plus })
        circularButton(gui.drawable { Ico_Back }, CircularButtonSize.L) {
            isDisabled = true
        }
        smallButton(translate { close }) {
            onClick {
                exitProcess(1)
            }
        }
        smallButton(string { "a" }, gui.colors.secondary) {
            isDisabled = true
        }
    }
}