package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext

@Scene2dDsl
class UIGrid(
    padding: Float,
    private val columns: Int
) : Table(), UIWidget<Unit> {
    private var storing = false
    private val actors: MutableList<Actor> = mutableListOf()

    override fun <T : Actor> storeActor(actor: T) {
        actors.add(actor)
        rebuild()
    }

    private fun rebuild() {
        storing = true
        clearChildren()
        storing = false

        actors.forEach { actor ->
            add(actor)
            newRowIfNecessary()
        }
    }

    private fun newRowIfNecessary() {
        if ((children.size) % columns == 0) {
            row()
        }
    }

    override fun removeActor(actor: Actor, unfocus: Boolean): Boolean {
        if (!storing) {
            actors.remove(actor)
            rebuild()
            return true
        }
        return false
    }

    @Scene2dCallbackDsl
    fun removeAll() {
        actors.toList().forEach(Actor::remove)
    }

    @Scene2dCallbackDsl
    fun remove(range: IntRange) {
        actors.subList(range.first, range.last).toList().forEach(Actor::remove)
    }

    init {
        touchable = Touchable.childrenOnly
        defaults().pad(padding)
    }


}


@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.grid(
    columns: Int,
    padding: Float = 0f,
    build: context(C) (@Scene2dDsl UIGrid).(S) -> Unit
): UIGrid =
    actor(UIGrid(padding, columns)) {
        build(it)
        pack()
    }
