package org.roldy.rendering.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.core.TimeManager

class ProxyScreen(
    val timeManager: TimeManager,
    val screen: Screen,
    camera: OrthographicCamera,
) : Screen by screen {
    val viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        screen.resize(width, height)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()
        screen.render(timeManager.getDelta(delta))
    }

}