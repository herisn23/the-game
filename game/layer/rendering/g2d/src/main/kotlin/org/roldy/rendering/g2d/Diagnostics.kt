package org.roldy.rendering.g2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.core.utils.invoke
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable

class Diagnostics : AutoDisposableAdapter() {
    private val batch by disposable { SpriteBatch() }
    private val shape by disposable { ShapeRenderer() }
    private val font by disposable { gameFont(size=12) }
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
        if(!enabled) return
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined

        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color.BLACK
        val height = diagnosticsProviders.size * yStep + yStep
        shape.rect(0f, Gdx.graphics.height - height, 200f, height)
        shape.end()

        batch {
            var y = 10f
            diagnosticsProviders.forEach {
                font.draw(this, it(), 10f, Gdx.graphics.height - y)
                y += yStep
            }
        }
    }
}