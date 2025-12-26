package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.Delegates

@Scene2dDsl
class KStructuredTooltip(
    background: Drawable,
    val table: KTable = KTable()
) : Tooltip<KTable>(table) {

    init {
        container.background = background
        manager.instant()
        manager.animations = false
        manager.initialTime = 0f
        container.addAction(object: Action() {
            override fun act(delta: Float): Boolean {
                if (container.hasParent() && container.stage != null) {
                    val mousePos = container.stage.screenToStageCoordinates(
                        Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
                    )
                    container.setPosition(mousePos.x + 10f, mousePos.y - container.height - 10f)
                }
                return false  // Nev
            }
        })
    }

    var padding by Delegates.observable(0f) { _, _, newValue ->
        table.pad(newValue)
    }

    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        if(table.children.size > 0)
        super.enter(event, x, y, pointer, fromActor)
    }

    @Scene2dCallbackDsl
    fun content(content: KTable.() -> Unit) {
        table.clear()
        table.content()
    }

    fun clean() {
        table.clear()
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <C : KContext> structuredTooltip(
    background: Drawable,
    init: context(C) (@Scene2dDsl KStructuredTooltip).() -> Unit = {}
): KStructuredTooltip {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return KStructuredTooltip(background).also {
        init(it)
    }
}