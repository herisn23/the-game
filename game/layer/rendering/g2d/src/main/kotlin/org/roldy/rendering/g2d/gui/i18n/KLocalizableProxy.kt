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
    return actor
}