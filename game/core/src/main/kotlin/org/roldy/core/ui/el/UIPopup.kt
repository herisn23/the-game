package org.roldy.core.ui.el

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import org.roldy.core.Vector2Int
import org.roldy.core.x
import kotlin.properties.Delegates

abstract class UIPopup : Container<Stack>(Stack()) {
    protected val table = UITable()
    protected val tmp = Vector2()
    var position: () -> Vector2Int = { 0 x 0 }

    var padding by Delegates.observable(0f) { _, _, newValue ->
        this.table.pad(newValue)
    }

    init {
        this.isVisible = false
        this.touchable = Touchable.childrenOnly
        this.actor.addActor(table)
    }

    override fun act(delta: Float) {
        super.act(delta)
        updatePosition()
    }

    protected abstract fun updatePosition()

    fun show(position: Vector2Int, content: UITable.(UIPopup) -> Unit) {
        toFront()
        table.clear()
        table.content(this)
        table.pack()
        actor.pack()
        validate()
        pack()

        showAt(position)
    }

    fun clean() {
        table.clear()
    }

    fun show(content: UITable.(UIPopup) -> Unit) {
        show(position(), content)
    }

    fun hide() {
        table.clear()
        pack()
        isVisible = false
    }

    fun showAt(screen: Vector2Int) {
        position = { screen }
        if (stage != null) {
            // Convert screen coords to stage coords
            updatePosition()
            this.isVisible = true
        }
    }

    internal fun calculatePosition(): Vector2 {
        val screen = position()
        val vec = stage.screenToStageCoordinates(tmp.set(screen.x.toFloat(), screen.y.toFloat()))
        return vec
    }
}