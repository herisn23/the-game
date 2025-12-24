package org.roldy.rendering.g2d.gui.i18n

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.core.i18n.I18N
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.TextActor

@Scene2dDsl
context(ctx: I18NContext)
fun <A> localizable(
    key: () -> I18N.Key,
    init: @Scene2dDsl (() -> String) -> A
): A where A : TextActor, A : Actor {
    val actor = init {
        ctx.i18n[key()]
    }
    ctx.i18n.addOnLocaleChangedListener(actor::updateText)
    return actor
}

data class KLocalizableProxy<A : Actor>(
    val actor: A,
    private val onRemove: () -> Unit
) {
    fun remove(): Boolean =
        actor.remove().also { onRemove() }
}

context(ctx: I18NContext)
fun <TA, A : Actor> TA.removable(actor: A) where TA : TextActor, TA : Actor =
    KLocalizableProxy(actor) {
        ctx.i18n.removeOnLocaleChangedListener(::updateText)
    }

context(ctx: I18NContext)
fun <TA> TA.removable() where TA : TextActor, TA : Actor =
    removable(this)