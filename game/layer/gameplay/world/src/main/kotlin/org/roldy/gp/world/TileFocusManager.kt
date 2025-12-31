package org.roldy.gp.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.Vector2Int
import org.roldy.core.utils.project
import org.roldy.core.x
import org.roldy.data.state.GameState
import org.roldy.gp.world.utils.Harvesting
import org.roldy.gui.WorldGUI
import org.roldy.gui.general.popup.data.minePopupContent
import org.roldy.rendering.map.WorldMap

class TileFocusManager(
    val gui: WorldGUI,
    val gameState: GameState,
    val camera: OrthographicCamera,
    val map: WorldMap
) {
    var lastFocus: Vector2Int? = null
    fun focusTile(coords: Vector2Int) {

        gui.hideTileInfo()

        if (lastFocus != null && lastFocus == coords) {
            lastFocus = null
            return
        }

        lastFocus = coords

        fun follow(): Vector2Int {
            val world = map.tilePosition.resolve(coords)
            camera.project(world)
            val flippedY = Gdx.graphics.height - world.y
            return world.x.toInt() x flippedY.toInt()
        }

        Harvesting.findMine(gameState, coords) { mine ->
            gui
                .showTileInfo(
                    { minePopupContent(it, mine) },
                    ::follow
                )
        }
    }
}