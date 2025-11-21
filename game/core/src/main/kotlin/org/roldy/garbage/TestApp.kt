package org.roldy.garbage

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.roldy.g2d.sprite.invoke
import org.roldy.listener.DefaultApplicationListener
import org.roldy.pawn.renderer.PawnRenderer

class TestApp(
    private val default: DefaultApplicationListener = DefaultApplicationListener()
) : ApplicationListener by default {
    private lateinit var pawnRenderer: PawnRenderer
    private lateinit var font: BitmapFont
    override fun create() {
        default.create()
        pawnRenderer = PawnRenderer()
        font = BitmapFont()
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        default.render()
        default.batch {
            context(delta, this) {
                pawnRenderer.render()
            }
            font.draw(default.batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
            font.draw(default.batch, "Delta: ${Gdx.graphics.deltaTime}", 10f, Gdx.graphics.height - 30f)
            font.draw(default.batch, "Memory: ${Gdx.app.javaHeap / 1024 / 1024} MB", 10f, Gdx.graphics.height - 50f)
        }
    }
}