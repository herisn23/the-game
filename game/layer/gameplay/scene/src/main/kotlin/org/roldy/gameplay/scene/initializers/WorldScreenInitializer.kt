package org.roldy.gameplay.scene.initializers

import org.roldy.core.i18n.I18N
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.state.HeroState
import org.roldy.gameplay.scene.GameLoader
import org.roldy.gameplay.scene.GameScene
import org.roldy.gameplay.scene.camera
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.screen.ProxyScreen
import java.io.File

fun GameScene.worldScreen(
    progress: (Float, I18N.Key) -> Unit
) {
    val saveFile = File("save_data.sav")
    val mapData = MapData(1L, MapSize.Small, 256)
    val camera = camera(0f)
    GameLoader(
        mapData,
        this,
        camera,
        HeroState(),
        saveFile,
        progress
    ) {
        Diagnostics.addProvider {
            "Game time: ${gameTime.value.formattedTime}"
        }
        processingLoop.value.start()
        setScreen(ProxyScreen(screen.value, camera))
    }
}