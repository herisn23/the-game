package org.roldy.gameplay.scene.initializers

import org.roldy.core.i18n.I18N
import org.roldy.gameplay.scene.GameScene
import org.roldy.gameplay.scene.camera
import org.roldy.rendering.screen.LoadingScreen
import org.roldy.rendering.screen.ProxyScreen


fun GameScene.loadingScreen(
    progress: ((Float, I18N.Key) -> Unit) -> Unit
) {
    val loading = LoadingScreen()
    progress(loading::setProgress)
    screen = ProxyScreen(loading.disposable(), camera())
}