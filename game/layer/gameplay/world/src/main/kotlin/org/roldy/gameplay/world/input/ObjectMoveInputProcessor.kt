package org.roldy.gameplay.world.input

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.Vector2Int
import org.roldy.core.keybind.KeybindName
import org.roldy.core.keybind.KeybindSettings
import org.roldy.core.utils.unproject
import org.roldy.core.x
import org.roldy.rendering.map.WorldMap

class ObjectMoveInputProcessor(
    val settings: KeybindSettings,
    val worldMap: WorldMap,
    val camera: OrthographicCamera,
    val updateCoords: (Vector2Int) -> Unit
) : InputAdapter() {

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button != settings[KeybindName.MoveTo]) return false
        val mousePos = screenX.toFloat() x screenY.toFloat()
        camera.unproject(mousePos)
        worldMap.tilePosition(
            mousePos.x x mousePos.y
        ) { coords, _, _ ->
            updateCoords(coords)
        }

        return false
    }
}