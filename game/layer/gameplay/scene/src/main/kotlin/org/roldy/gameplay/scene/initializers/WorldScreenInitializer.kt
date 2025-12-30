package org.roldy.gameplay.scene.initializers

import org.roldy.core.TimeManager
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.core.i18n.I18N
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.gameplay.scene.GameLoader
import org.roldy.gameplay.scene.GameScene
import org.roldy.gameplay.scene.GameTime
import org.roldy.gameplay.scene.camera
import org.roldy.gp.world.processor.RefreshingProcessor
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.screen.ProxyScreen
import java.io.File

fun GameScene.worldScreen(
    timeManager: TimeManager,
    processingLoop: DeltaProcessingLoop,
    progress: (Float, I18N.Key) -> Unit
) {
    val saveFile = File("save_data.sav")
    val mapData = MapData(1L, MapSize.Small, 256)
    val camera = camera(3f)
    GameLoader(
        mapData,
        this,
        camera,
        timeManager,
        saveFile,
        progress
    ) {
        val refreshingProcessor = RefreshingProcessor(gameState.value)
        val gameTime = GameTime(gameState.value.time)

        Diagnostics.addProvider {
            "Game time: ${gameTime.formattedTime}"
        }

        processingLoop.addConsumer(refreshingProcessor)
        processingLoop.addConsumer {
            gameTime.update()
            gameState.value.time = gameTime.time
        }

        setScreen(ProxyScreen(screen.value, camera))
    }
}