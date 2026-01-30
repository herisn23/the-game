package org.roldy.scene

import com.badlogic.gdx.Gdx
import org.roldy.core.CursorManager
import org.roldy.core.Logger
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.core.disposable.AutoDisposableGameAdapter
import org.roldy.core.time.TimeManager
import org.roldy.rendering.screen.test.Screen3D

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
        screen = Screen3D(camera3D())

    }

    override fun render() {
        screen?.apply {
            render(timeManager.getDelta(Gdx.graphics.deltaTime))
        }
    }


    override fun dispose() {
        super.dispose()
        processingLoop.cancel()
    }
}