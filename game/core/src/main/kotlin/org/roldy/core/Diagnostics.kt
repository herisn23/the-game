package org.roldy.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.core.Logger.Level
import org.roldy.utils.invoke

class Diagnostics {
    private val batch by lazy {
        SpriteBatch()
    }

    private val shape by lazy {
        ShapeRenderer()
    }

    private val font by lazy {
        BitmapFont()
    }


    init {
        Logger.level = Level.Debug
    }

    companion object {
        private val diagnosticsProviders: MutableList<() -> String> by lazy {
            val profiler = GLProfiler(Gdx.graphics)
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
//                add {
//                    "Draw calls: ${profiler.drawCalls}"
//                }
//                add {
//                    "Texture bindings: ${profiler.textureBindings}"
//                }
//                add {
//                    "Shader switches : ${ profiler.shaderSwitches }"
//                }
//                add {
//                    "Vertices: ${ profiler.vertexCount.total }"
//                }
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

    fun dispose() {
        batch.dispose()
        font.dispose()
        shape.dispose()
    }
}