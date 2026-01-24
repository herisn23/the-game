package org.roldy.core.ui.el

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.ui.Scene2dCallbackDsl
import org.roldy.core.ui.Scene2dDsl
import org.roldy.core.ui.UIContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.Delegates

@Scene2dDsl
class UIStructuredTooltip(
    background: Drawable,
    val table: UITable = UITable()
) : Tooltip<UITable>(table) {

    init {
        container.background = background
        manager.instant()
        manager.animations = false
        manager.initialTime = 0f
        container.addAction(object: com.badlogic.gdx.scenes.scene2d.Action() {
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
    fun content(content: UITable.() -> Unit) {
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
fun <C : UIContext> structuredTooltip(
    background: Drawable,
    init: context(C) (@Scene2dDsl UIStructuredTooltip).() -> Unit = {}
): UIStructuredTooltip {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return UIStructuredTooltip(background).also {
        init(it)
    }
}