package org.roldy

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.roldy.core.Logger
import org.roldy.core.Logger.Level
import org.roldy.environment.EnvironmentalObject

class TestApp : ApplicationAdapter() {
    lateinit var diagnostic: Diagnostic
    lateinit var pawnTest: PawnTest
    lateinit var terrainTest: TerrainTest
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport
    lateinit var layeringRenderer: LayeringRenderer
    lateinit var streamTest: StreamTest

    override fun create() {
        Logger.level = Level.Debug
        diagnostic = Diagnostic()
        val objects = mutableListOf<EnvironmentalObject>()
        camera = OrthographicCamera().apply {
            zoom = 0f
            position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
        }
        terrainTest = TerrainTest(15f)
        pawnTest = PawnTest(10f, camera)
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
//        layeringRenderer = LayeringRenderer(
//            objects + pawnTest, camera
//        )
        streamTest = StreamTest(camera)
    }

    private fun generatedObjectsByTerrain() {

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
            terrainTest.render()
            streamTest.render()
            pawnTest.render()
        }

        diagnostic.render()
    }

    override fun dispose() {
        diagnostic.dispose()
        pawnTest.dispose()
    }
}