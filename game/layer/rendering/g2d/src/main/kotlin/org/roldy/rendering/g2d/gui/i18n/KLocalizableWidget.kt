package org.roldy.rendering.g2d.gui.i18n

import org.roldy.core.i18n.I18N
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.Text

@Scene2dDsl
context(ctx: I18NContext)
fun <A : Text> localizable(
    key: () -> I18N.Key,
    init: @Scene2dDsl (() -> String) -> A
): A {
    val actor = init {
        ctx.i18n[key()]
    }
    ctx.i18n.addOnLocaleChangedListener(actor::updateText)
    return actor
}
