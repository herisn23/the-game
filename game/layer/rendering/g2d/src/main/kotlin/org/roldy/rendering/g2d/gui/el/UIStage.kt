package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.core.Vector2Int
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext

@Scene2dDsl
class UIStage(
    scale: Float,
    private val referenceResolution: Vector2Int
) : Stage(), UIWidget<Unit> {

    val defaultWidth get() = Gdx.graphics.width.toFloat() / Gdx.graphics.density
    val defaultHeight get() = Gdx.graphics.height.toFloat() / Gdx.graphics.density

    init {
        root.name = "Stage"
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
    private var touchDownHandled = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touchDownHandled = super.touchDown(screenX, screenY, pointer, button)
        return touchDownHandled
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val handled = super.touchUp(screenX, screenY, pointer, button)
        // If touchDown was handled, consume touchUp too
        return handled || touchDownHandled.also { touchDownHandled = false }
    }
}


@Scene2dDsl
context(_: C)
fun <C : UIContext> stage(
    scale: Float = 1f,
    referenceResolution: Vector2Int,
    build: context(C) (@Scene2dDsl UIStage).() -> Unit
): UIStage =
    UIStage(scale, referenceResolution).also {
        it.build()
    }


