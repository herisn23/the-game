package org.roldy.gui

import com.badlogic.gdx.Gdx
import org.roldy.rendering.g2d.Gui
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter


class TestGui : AutoDisposableAdapter(), Gui {


    val stage = gui {
        button("Just ty something") {
            print("click")
        }.apply {
            setPosition(1000f, 400f)
        }
    }

    init {
        // Make everything appear 50% smaller
        stage.viewport.camera.viewportWidth = Gdx.graphics.width * 2f
        stage.viewport.camera.viewportHeight = Gdx.graphics.height * 2f
        stage.viewport.camera.position.set(
            stage.viewport.camera.viewportWidth / 2f,
            stage.viewport.camera.viewportHeight / 2f,
            0f
        )
        stage.viewport.camera.update()
    }

    context(delta: Float)
    override fun render() {
        stage.act(delta)
        stage.draw()
    }
}