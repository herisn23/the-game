package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
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
    }

    var padding by Delegates.observable(0f) { _, _, newValue ->
        table.pad(newValue)
    }

    @Scene2dCallbackDsl
    fun content(content: KTable.() -> Unit) {
        table.clear()
        table.content()
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