package org.roldy.garbage

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import org.roldy.listener.DefaultApplicationListener
import org.roldy.pawn.renderer.PawnRenderer

class TestApp(
    private val default: DefaultApplicationListener = DefaultApplicationListener()
) : ApplicationListener by default {
    lateinit var pawnRenderer: PawnRenderer
    override fun create() {
        default.create()
        pawnRenderer = PawnRenderer()
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        default.render()
        pawnRenderer.render(delta, default.batch)
    }
}