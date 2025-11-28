package org.roldy

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class TestApp : ApplicationAdapter() {
    lateinit var diagnostic: Diagnostic
    lateinit var pawnTest: PawnTest
    lateinit var terrainTest: TerrainTest
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport

    override fun create() {
        diagnostic = Diagnostic()
        pawnTest = PawnTest(100f)
        terrainTest = TerrainTest(10f)
        camera = OrthographicCamera().apply {
            zoom = 20f
        }
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        diagnostic.resize(width, height)
    }


    override fun render() {
        ScreenUtils.clear(Color.DARK_GRAY)//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()

        context(Gdx.graphics.deltaTime, camera) {
            terrainTest.render()
            pawnTest.render()
        }

        diagnostic.render()
    }

    override fun dispose() {
        diagnostic.dispose()
        pawnTest.dispose()
    }
}