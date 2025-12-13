package org.roldy.gameplay.scene

import org.roldy.gameplay.scene.initializers.createWorldScreen
import org.roldy.core.Logger
import org.roldy.rendering.g2d.disposable.AutoDisposableGameAdapter

class GameScene: AutoDisposableGameAdapter() {
    override fun create() {
        Logger.level = Logger.Level.Debug
        setScreen(createWorldScreen())
    }
}