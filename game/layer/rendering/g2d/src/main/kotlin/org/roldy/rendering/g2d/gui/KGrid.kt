package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table

@Scene2dDsl
class KGrid(
    padding: Float,
    private val columns: Int
) : Table(), KWidget<Unit> {
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
            val cell = add(actor)
            newRowIfNecessary()
            actor.userObject = cell
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
        defaults().pad(padding)
    }


}


@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.grid(
    columns: Int,
    padding: Float = 0f,
    build: context(C) (@Scene2dDsl KGrid).(S) -> Unit
): KGrid =
    actor(KGrid(padding, columns)) {
        build(it)
        pack()
    }
