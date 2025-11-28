package org.roldy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.utils.invoke

class Diagnostic {
    private val batch by lazy {
        SpriteBatch()
    }

    private val font by lazy {
        BitmapFont()
    }

    private val camera by lazy {
        OrthographicCamera()
    }

    private val viewport by lazy {
        FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    fun render() {
        batch.projectionMatrix = camera.combined
        batch {
            font.draw(this, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
            font.draw(this, "Delta: ${Gdx.graphics.deltaTime}", 10f, Gdx.graphics.height - 30f)
            font.draw(this, "Memory: ${Gdx.app.javaHeap / 1024 / 1024} MB", 10f, Gdx.graphics.height - 50f)
        }
    }

    fun dispose() {
        batch.dispose()
    }
}