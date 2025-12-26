package org.roldy.gameplay.scene

import org.roldy.core.CursorManager
import org.roldy.core.Logger
import org.roldy.core.TimeManager
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.gameplay.scene.initializers.createWorldScreen
import org.roldy.rendering.g2d.disposable.AutoDisposableGameAdapter

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
        setScreen(createWorldScreen(timeManager, processingLoop))
    }


    override fun dispose() {
        super.dispose()
        processingLoop.cancel()
    }
}