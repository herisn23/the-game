package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.core.i18n.I18N
import org.roldy.core.i18n.Strings
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.TextActor
import org.roldy.rendering.g2d.gui.i18n.localizable

typealias TextManagerClosure<S> = () -> S

data class TextManager(
    val text: TextManagerClosure<String>? = null,
    val translate: TextManagerClosure<I18N.Key>? = null
) {


    @Scene2dCallbackDsl
    context(gui: GuiContext)
    operator fun <A> invoke(button: (() -> String) -> A): A where A : TextActor, A : Actor =
        when {
            translate != null -> localizable(translate) { string ->
                button(string)
            }

            text != null -> button(text)

            else -> error("ButtonText must contain text or translate")
        }
}

@Scene2dCallbackDsl
fun translate(translate: Strings.() -> I18N.Key) =
    TextManager(
        translate = { Strings.translate() }
    )

@Scene2dCallbackDsl
fun string(text: () -> String) =
    TextManager(
        text = text
    )