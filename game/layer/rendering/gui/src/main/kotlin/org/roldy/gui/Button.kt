package org.roldy.gui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import org.roldy.core.utils.alpha
import org.roldy.core.utils.brighter
import org.roldy.core.utils.get
import org.roldy.core.utils.transparentColor
import org.roldy.rendering.g2d.NinePatchParams
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.g2d.gameFont
import org.roldy.rendering.g2d.ninePatch

context(atlas: TextureAtlas, color: GUIColors)
fun AutoDisposable.button(
    text: String,
    width: Float = 735f,
    height: Float = 210f,
    onClick: () -> Unit): Actor {
    val font by disposable {
        gameFont(size = 32) {
            padTop = 0
            padBottom = 0
            this.color = color.tint
        }
    }
    val stretch = 30f
    val ninePatchParams = NinePatchParams(50, 50, 30, 30)
    return table { table ->
        setSize(width, height)
//        setPosition(1000f, 100f)

        //background
        ninePatch(atlas["Button_RL_Background"], ninePatchParams, ::Image).apply {
            setSize(table.width, table.height)
            setPosition(0f, 0f)
        }.let(::addActor)

        //foreground
        ninePatch(atlas["Button_RL_Foreground"], ninePatchParams, ::Image).apply {
            stretch(
                table.width,
                table.height,
                stretch = 30f
            )
            this.color = color.default
        }.let(::addActor)

        //overlay 1
        ninePatch(atlas["Button_RL_Overlay1"], 0, 0, 13, 0, ::Image).apply {
            stretch(
                table.width,
                76f,
                bottom = 100f
            )
            this.color = transparentColor(0.5f)
        }.let(::addActor)

        //overlay 2
        ninePatch(atlas["Button_RL_Overlay2"], 30, 46, 27, 30, ::Image).apply {
            stretch(
                table.width,
                table.height,
                left = stretch - 1,
                right = stretch - 1,
                top = stretch + 2,
                bottom = stretch + 4
            )
            this.color = (color.default brighter 1.4f) alpha 0.75f
        }.let(::addActor)

        //button
        ninePatch(atlas["Button_RL_Hover"], left = 56, right=56) {
            val style = TextButton.TextButtonStyle().apply {
                this.up = tint(transparentColor(0f))
                this.down = tint(color.default alpha 0.25f)
                this.over = tint(color.default alpha 0.15f)
                this.font = font
            }
            TextButton(
                text,
                style
            ).apply {
                padTop(-10f) //adjust font to center of button
                stretch(
                    table.width,
                    table.height,
                    stretch + 3
                )
                addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        onClick()
                    }
                })
            }
        }.let(::addActor)
    }
}