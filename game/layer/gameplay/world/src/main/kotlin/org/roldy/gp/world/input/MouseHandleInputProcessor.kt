package org.roldy.gp.world.input

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.Vector2Int
import org.roldy.core.keybind.KeybindName
import org.roldy.core.keybind.KeybindSettings
import org.roldy.core.logger
import org.roldy.core.utils.unproject
import org.roldy.core.x
import org.roldy.rendering.map.WorldMap

class MouseHandleInputProcessor(
    val settings: KeybindSettings,
    val worldMap: WorldMap,
    val camera: OrthographicCamera,
    val moveTo: (Vector2Int) -> Unit,
    val focusOn: (Vector2Int) -> Unit
) : InputAdapter() {

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        logger.info { "Received touch up $screenX, $screenY, $pointer" }
        return button.onTouch(KeybindName.MoveTo, screenX, screenY, moveTo) ||
                button.onTouch(KeybindName.TileFocus, screenX, screenY) {
                    focusOn(it)
                }
    }

    fun Int.onTouch(keybind: KeybindName, screenX: Int, screenY: Int, coords: (Vector2Int) -> Unit): Boolean {
        if (this != settings[keybind]) return false
        val mousePos = screenX.toFloat() x screenY.toFloat()
        camera.unproject(mousePos)
        worldMap.tilePosition(
            mousePos.x x mousePos.y
        ) { coords, _, _ ->
            coords(coords)
        }
        return true
    }
}