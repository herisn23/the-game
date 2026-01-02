package org.roldy.gui.widget

import org.roldy.data.mine.harvestable.Gem
import org.roldy.gui.CraftingIconTexturesType
import org.roldy.gui.GuiContext
import org.roldy.gui.general.LabelActions
import org.roldy.gui.general.icon
import org.roldy.gui.general.label
import org.roldy.gui.itemBackground
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.*


class ItemInfo(
    val upperText: LabelActions,
    val lowerText: LabelActions,
    val icon: UIImage
)

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.itemInfo(init: ItemInfo.(UITable, S) -> Unit) =
    table(true) { storage ->
        lateinit var upperText: LabelActions
        lateinit var lowerText: LabelActions
        lateinit var icon: UIImage
        image(itemBackground { this })
        table {
            val padding = 20f
            val labelPadding = 16f
            pad(padding)
            icon(gui.craftingIcons.drawable(Gem.Sapphire, CraftingIconTexturesType.Background)) {
                icon = this
            }
            table {
                it.growX()
                    .padLeft(padding)
                    .padTop(labelPadding)
                    .padBottom(labelPadding)
                    .padRight(labelPadding)
                    .uniform()
                    .center()
                    .left()

                label(color = gui.colors.primaryText) {
                    it.left().center().growX()
                    setText("Upper text")
                    upperText = this
                }
                row()
                label() {
                    it.left().center().growX()
                    setText("Lower text")
                    lowerText = this
                }
                ItemInfo(upperText, lowerText, icon).init(this, storage)
            }
        }
    }