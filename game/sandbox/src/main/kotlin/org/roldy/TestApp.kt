package org.roldy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.roldy.core.Diagnostics
import org.roldy.core.disposable.AutoDisposableApplicationAdapter
import org.roldy.core.disposable.disposable
import org.roldy.scene.Scene
import org.roldy.scene.world.WorldScene

class TestApp : AutoDisposableApplicationAdapter() {
    lateinit var diagnostic: Diagnostics
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport
    lateinit var currentScene: Scene

    override fun create() {
        diagnostic = disposable(Diagnostics())
        camera = OrthographicCamera().apply {
            zoom = 100f
            position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
        }
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)

        currentScene = disposable(WorldScene(camera))
        currentScene.onShow()

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        diagnostic.resize(width, height)
    }


    override fun render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()

        context(Gdx.graphics.deltaTime, camera) {
            currentScene.render()
        }

        diagnostic.render()

        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            System.gc()
            Thread.sleep(100)
        }
    }
}