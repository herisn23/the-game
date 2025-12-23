package org.roldy.gui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.roldy.core.utils.get
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.KWidget
import org.roldy.rendering.g2d.gui.NinePatchParams
import org.roldy.rendering.g2d.gui.ninePatch
import org.roldy.rendering.g2d.gui.table


@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.tooltip(
    width: Float = 10f,
    height: Float = 10f,
    autosize: Boolean = true
): Actor {
    val ninePatchParams = NinePatchParams(25, 25, 25, 25)
    return table {
//        //tooltip background
//        ninePatch(gui.atlas["Tooltip_Background"], ninePatchParams, ::Image) {
//            setSize(width, height)
//            setPosition(0f, 0f)
//        }
    }
}