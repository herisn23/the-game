package org.roldy.gameplay.scene

import org.roldy.core.CursorManager
import org.roldy.core.Logger
import org.roldy.core.TimeManager
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.gameplay.scene.dev.TestTiledMapBlendScreen
import org.roldy.rendering.g2d.disposable.AutoDisposableGameAdapter
import org.roldy.rendering.screen.ProxyScreen

class GameScene : AutoDisposableGameAdapter() {


    val timeManager by lazy {
        TimeManager()
    }
    val processingLoop by lazy {
        DeltaProcessingLoop(timeManager)
    }

    override fun create() {
        Logger.level = Logger.Level.Debug
        CursorManager.initialize()
        val camera = camera(30f)
        screen = ProxyScreen(TestTiledMapBlendScreen(camera), camera)
//        screen = ProxyScreen(DebugTiledMapScreen(camera), camera)


//        loadingScreen { setProgress ->
//            worldScreen(setProgress)
//        }

    }


    override fun dispose() {
        super.dispose()
        processingLoop.cancel()
    }
}