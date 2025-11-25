package org.roldy.listener

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

class DefaultApplicationListener : ApplicationAdapter() {
    lateinit var viewport: FitViewport
    lateinit var batch: SpriteBatch
    lateinit var camera: OrthographicCamera
    override fun create() {
        camera = OrthographicCamera()
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
        batch = SpriteBatch()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render() {
        ScreenUtils.clear(Color.DARK_GRAY)//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()
        batch.setProjectionMatrix(viewport.camera.combined)
    }

    override fun dispose() {
        batch.dispose()
    }
}