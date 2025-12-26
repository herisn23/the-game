package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.brighter
import org.roldy.core.utils.sequencer
import org.roldy.data.item.ItemGrade
import org.roldy.gui.button.mainButton
import org.roldy.gui.button.smallButton
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import kotlin.contracts.ExperimentalContracts
import kotlin.random.Random
import kotlin.system.exitProcess

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.example() {
    image(emptyImage(Color.BLACK brighter 1.5f)) {
        setSize(stage.width, stage.height)
    }
    val playerInventory = (0..30).map {
        Random.nextInt(0, 200) to ItemGrade.entries.random()
    }.toMutableList()

    fun data() =
        playerInventory.map {
            data(
                gui.drawable { Icon_Sword_128 },
                it.second,
                it.first,
                "Item tooltip: ${it.second}"
            ) { data->
                pad(400f)
                label(string { data.grade?.name ?: "" }, 60) {
                    color = data.grade?.color ?: Color.WHITE
                }
            }
        }

    val seq by sequencer(1, 2)

    val window = inventory(
        listOf(
            sort(string { "Sort 1" }) {

                playerInventory.sortBy {
                    if (seq.current == 1) {
                        it.second.ordinal
                    } else {
                        -it.second.ordinal
                    }
                }
                it.setData(data())
                seq.next()
            },
            sort(string { "Sort 2" }) {
                playerInventory.sortBy {
                    if (seq.current == 2) {
                        it.first
                    } else {
                        -it.first
                    }
                }
                it.setData(data())
                seq.next()
            }
        )
    ) {
        setData(data())
        maxSlots = 50
        onSlotClick {

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