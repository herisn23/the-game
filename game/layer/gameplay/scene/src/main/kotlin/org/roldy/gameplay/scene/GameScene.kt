package org.roldy.gameplay.scene

import org.roldy.gameplay.scene.initializers.createWorldScreen
import org.roldy.core.Logger
import org.roldy.core.TimeManager
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.rendering.g2d.disposable.AutoDisposableGameAdapter

class GameScene: AutoDisposableGameAdapter() {
    val timeManager by lazy {
        TimeManager()
    }
    val processingLoop by lazy {
        DeltaProcessingLoop(timeManager)
    }

    override fun create() {
        Logger.level = Logger.Level.Debug
        setScreen(createWorldScreen(timeManager, processingLoop))
    }
}