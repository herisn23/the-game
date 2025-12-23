package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScalingViewport
import kotlin.properties.Delegates

@Scene2dDsl
class KStage(scale: Float) : Stage(), KWidget<Unit> {

    var scale: Float by Delegates.observable(scale) { prop, old, new ->
        updateViewport(new)
    }

    init {
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        this.scale = scale
    }

    private fun updateViewport(scale: Float) {
        (viewport.camera as OrthographicCamera).zoom = 1f / scale
    }

    override fun <T : Actor> storeActor(actor: T): Unit =
        addActor(actor)

    val screenWidth: Float get() = viewport.camera.viewportWidth
    val screenHeight: Float get() = viewport.camera.viewportHeight
}


@Scene2dDsl
context(_: C)
fun <C : KContext> stage(scale: Float = 1f, build: context(C) (@Scene2dDsl KStage).() -> Unit): KStage =
    KStage(scale).also {
        it.build()
    }


