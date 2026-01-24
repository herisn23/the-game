package org.roldy.rendering.screen

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.i18n.I18N
import org.roldy.core.utils.invoke
import org.roldy.gui.LoadingGUI
import org.roldy.rendering.g2d.disposable.AutoDisposableScreenAdapter
import org.roldy.rendering.g2d.disposable.disposable

class LoadingScreen : AutoDisposableScreenAdapter() {
    val gui by disposable { LoadingGUI() }

    val batch by disposable { SpriteBatch() }

    val background = TextureRegion(Texture("Background.png").disposable())

    fun setProgress(progress: Float, key: I18N.Key) {
        gui.setProgress(progress, key)
    }

    override fun resize(width: Int, height: Int) {
        gui.resize(width, height)
    }

    override fun render(delta: Float) {
        batch {
            draw(background, 0f, 0f)
        }
        with(delta) {
            gui.render()
        }
    }
}