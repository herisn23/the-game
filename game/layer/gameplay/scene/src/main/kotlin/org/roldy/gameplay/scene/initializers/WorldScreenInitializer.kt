package org.roldy.gameplay.scene.initializers

import org.roldy.core.i18n.I18N
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.state.HeroState
import org.roldy.gameplay.scene.GameScene
import org.roldy.gameplay.scene.camera
import org.roldy.gameplay.scene.gameLoader
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.screen.ProxyScreen
import java.io.File

fun GameScene.worldScreen(
    progress: (Float, I18N.Key) -> Unit
) {
    val saveFile = File("save_data.sav")
    //-1505060409
    val mapData = MapData(-1505060409, MapSize.Small, 256)
    val camera = camera(1f)
    gameLoader(
        saveFile,
        camera,
        { HeroState() },
        { mapData },
        progress
    ) {
        Diagnostics.addProvider {
            "Game time: ${gameTime.value.formattedTime}"
        }
        processingLoop.value.start()
        setScreen(ProxyScreen(screen.value, camera))
        println("seed: ${mapData.seed}")
    }
}