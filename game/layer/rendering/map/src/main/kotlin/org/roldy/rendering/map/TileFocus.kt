package org.roldy.rendering.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.utils.unproject
import org.roldy.core.x
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter

class TileFocus(
    private val tilePositionResolver: TilePositionResolver
) : AutoDisposableAdapter() {
    val highlightTexture = Texture("HexTileHighlighter.png").disposable()
    private val batch = SpriteBatch().disposable()
    var focusedTile: Vector2Int? = null
    var focusTilePosition: Vector2? = null


    fun render(camera: OrthographicCamera) {
        handleHexClick(camera)
        renderFocusTile(camera)
    }

    fun renderFocusTile(camera: OrthographicCamera) {
        focusTilePosition?.run {
            batch.projectionMatrix = camera.combined
            batch.begin()
            batch.draw(highlightTexture, x, y)
            batch.end()
        }
    }


    fun handleHexClick(camera: OrthographicCamera) {
        if (Gdx.input.justTouched()) {
            val vec = Gdx.input.x.toFloat() x Gdx.input.y.toFloat()
            camera.unproject(vec)
            tilePositionResolver(vec.x x vec.y, false, {
                //unfocus when click away from map
                focusTilePosition = null
                focusedTile = null
            }) { coords, position, _ ->
                when {
                    coords == focusedTile -> {
                        focusTilePosition = null
                        focusedTile = null
                    }

                    else -> {
                        focusTilePosition = position
                        focusedTile = coords
                    }
                }
            }
        }
    }
}