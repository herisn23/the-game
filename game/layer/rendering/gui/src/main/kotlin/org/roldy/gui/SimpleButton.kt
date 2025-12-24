package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.roldy.core.utils.alpha
import org.roldy.core.utils.transparentColor
import org.roldy.rendering.g2d.gui.KTextButton
import org.roldy.rendering.g2d.gui.KWidget
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.textButton

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.simpleButton(): KTextButton {
    val font = gui.font(16) {
        padTop = 0
        padBottom = 0
    }

    fun a(color: Color) = Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
        setColor(color)
        fill()
        TextureRegionDrawable(Texture(this)).also {
            dispose()
        }
    }

    val style = TextButton.TextButtonStyle().apply {
        this.up = a(gui.colors.default)
        this.down = a(gui.colors.default alpha 0.25f)
        this.over = a(gui.colors.default alpha 0.15f)
        this.font = font
    }
    return textButton({ "aha bacha" }, style) {
        setSize(200f, 200f)
//        padTop(-10f) //adjust font to center of button
    }
}