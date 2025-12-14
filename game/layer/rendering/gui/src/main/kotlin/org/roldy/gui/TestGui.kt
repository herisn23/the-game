package org.roldy.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import org.roldy.core.asset.AtlasLoader
import org.roldy.rendering.g2d.Gui
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.g2d.gameFont


class TestGui : AutoDisposableAdapter(), Gui {


    val stage by disposable { Stage() }
    val font by disposable { gameFont() }


//    class StackableDrawable: BaseDrawable {
//
//    }

    init {
        val table = Table()
//        table.setFillParent(true)
//        table.align(Align.left)
        table.setSize(735f, 210f)
        table.setPosition(1000f, 100f)

        val atlas = AtlasLoader.gui.disposable()

        // Define border sizes (in pixels)
        val left = 50    // Left border width
        val right = 50   // Right border width
        val top = 30     // Top border height
        val bottom = 30  // Bottom border height

        val ninePatch = NinePatch(atlas.findRegion("Button_RL_Foreground"), left, right, top, bottom)
        val drawable = NinePatchDrawable(ninePatch)

        atlas.findRegion("Button_RL_Background").let {
            val ninePatch = NinePatch(it, left, right, top, bottom)
            val drawable = NinePatchDrawable(ninePatch)
            val icon = Image(drawable)
            icon.setSize(table.width, table.height)
            icon.setPosition(0f, 0f)
            table.addActor(icon)
        }

        val style = TextButton.TextButtonStyle().apply {
            up = drawable.tint(Color.RED)
            down = drawable.tint(Color.BLUE)
            over = drawable.tint(Color.YELLOW)
            font = this@TestGui.font
        }


        val textButton = TextButton(
            "Click Me",
            style
        )
        val stretch = 30f
        textButton.setSize(table.width - stretch*2, table.height-stretch*2)
        textButton.setPosition(stretch, stretch)
        table.addActor(textButton)


        textButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                println("clicked")
            }
        })

        // Make everything appear 50% smaller
        stage.viewport.camera.viewportWidth = Gdx.graphics.width * 2f
        stage.viewport.camera.viewportHeight = Gdx.graphics.height * 2f
        stage.viewport.camera.position.set(
            stage.viewport.camera.viewportWidth / 2f,
            stage.viewport.camera.viewportHeight / 2f,
            0f
        )
        stage.viewport.camera.update()
        stage.addActor(table)
    }

    context(delta: Float)
    override fun render() {
        stage.act(delta)
        stage.draw()
    }
}