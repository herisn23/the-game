package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.core.Vector2Int
import kotlin.properties.Delegates

@Scene2dDsl
class KStage(
    scale: Float,
    private val referenceResolution: Vector2Int
) : Stage(), KWidget<Unit> {

    var scale: Float by Delegates.observable(scale) { prop, old, new ->
        updateViewport(new)
    }

    val defaultWidth get() = Gdx.graphics.width.toFloat() / Gdx.graphics.density
    val defaultHeight get() = Gdx.graphics.height.toFloat() / Gdx.graphics.density

    init {
        viewport = FitViewport(defaultWidth, defaultHeight)
        updateViewport(scale)
    }

    private fun calculateScale(): Float {
        // Calculate scale factor
        val scaleX = defaultWidth / referenceResolution.x
        val scaleY = defaultHeight / referenceResolution.y

        // Use minimum to prevent UI overflow
        return minOf(scaleX, scaleY).coerceIn(0.5f, 2f) // Limit scale range
    }

    private fun updateViewport(scale: Float) {
        val realScale = calculateScale() * scale
        viewport.setWorldSize(defaultWidth / realScale, defaultHeight / realScale)
    }

    override fun <T : Actor> storeActor(actor: T): Unit =
        addActor(actor)

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
//        updateViewport(scale)
    }
}


@Scene2dDsl
context(_: C)
fun <C : KContext> stage(
    scale: Float = 1f,
    referenceResolution: Vector2Int,
    build: context(C) (@Scene2dDsl KStage).() -> Unit
): KStage =
    KStage(scale, referenceResolution).also {
        it.build()
    }


