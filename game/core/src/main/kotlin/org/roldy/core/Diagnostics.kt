package org.roldy.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.utils.invoke

class Diagnostics() : AutoDisposableAdapter() {
    private val batch by disposable { SpriteBatch() }
    private val shape by disposable { ShapeRenderer() }
    private val font by disposable { BitmapFont() }
    var enabled = true

    companion object {
        private val diagnosticsProviders: MutableList<() -> String> by lazy {
            mutableListOf<() -> String>().apply {
                add {
                    "FPS: ${Gdx.graphics.framesPerSecond}"
                }
                add {
                    "Delta: ${Gdx.graphics.deltaTime}"
                }
                add {
                    "Memory: ${Gdx.app.javaHeap / 1024 / 1024} MB"
                }
            }
        }

        fun addProvider(provider: () -> String) {
            diagnosticsProviders.add(provider)
        }
    }

    private val camera by lazy {
        OrthographicCamera()
    }

    private val viewport by lazy {
        FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    val yStep = 20f
    fun render() {
        if (!enabled) return
        shape.projectionMatrix = camera.combined

        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        // Reset projection matrix to screen coordinates
        shape.projectionMatrix.setToOrtho2D(0f, 0f, screenWidth, screenHeight)
        shape.updateMatrices()

        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color.BLACK
        val height = diagnosticsProviders.size * yStep + yStep
        shape.rect(0f, screenHeight - height, 200f, height)

        shape.end()
        batch {
            var y = 10f
            diagnosticsProviders.forEach {
                font.draw(this, it(), 10f, screenHeight - y)
                y += yStep
            }
        }
    }
}