package org.roldy.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import org.roldy.core.asset.loadAsset

object CursorManager {
    private var isVisible = true
    private val invisibleCursor: Cursor = run {
        // Create invisible cursor once
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0f)
        pixmap.fill()
        Gdx.graphics.newCursor(pixmap, 0, 0).also {
            pixmap.dispose()
        }
    }
    val cursor: Cursor = run {
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

    fun initialize() {
        isVisible = true
        Gdx.graphics.setCursor(cursor)
    }

    fun hide() {
        if (isVisible) {
            Gdx.graphics.setCursor(invisibleCursor)
            isVisible = false
        }
    }

    fun show() {
        if (!isVisible) {
            Gdx.graphics.setCursor(cursor)
            isVisible = true
        }
    }

    fun toggle() {
        if (isVisible) hide() else show()
    }

    fun dispose() {
        invisibleCursor.dispose()
        cursor.dispose()
    }
}