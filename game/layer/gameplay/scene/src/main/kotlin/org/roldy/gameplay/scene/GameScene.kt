package org.roldy.gameplay.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import org.roldy.core.Logger
import org.roldy.core.TimeManager
import org.roldy.core.asset.loadAsset
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.gameplay.scene.initializers.createWorldScreen
import org.roldy.rendering.g2d.disposable.AutoDisposableGameAdapter
import org.roldy.rendering.g2d.disposable.disposable

class GameScene : AutoDisposableGameAdapter() {

    val cursor: Cursor by disposable {
        val scale = .5f
        val original = Pixmap(loadAsset("Cursor_Normal.png"))
        val newWidth = (original.width * scale).toInt()
        val newHeight = (original.height * scale).toInt()
        val scaled = Pixmap(newWidth, newHeight, original.format)


// Draw the original pixmap scaled into the new one
        scaled.setFilter(Pixmap.Filter.BiLinear) // or NearestNeighbour for pixel art
        scaled.drawPixmap(
            original,
            0, 0, original.getWidth(), original.getHeight(),  // source
            0, 0, newWidth, newHeight
        ) // destination


// Create cursor from scaled pixmap
        Gdx.graphics.newCursor(scaled, 0, 0).also {
            scaled.dispose()
            original.dispose()
        }
    }

    val timeManager by lazy {
        TimeManager()
    }
    val processingLoop by lazy {
        DeltaProcessingLoop(timeManager)
    }

    override fun create() {
        Logger.level = Logger.Level.Debug
        Gdx.graphics.setCursor(cursor)
        setScreen(createWorldScreen(timeManager, processingLoop))
    }


    override fun dispose() {
        super.dispose()
        processingLoop.cancel()
    }
}