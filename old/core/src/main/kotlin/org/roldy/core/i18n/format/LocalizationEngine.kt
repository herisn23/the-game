package org.roldy.core.i18n.format

import org.roldy.core.i18n.format.handler.composite
import org.roldy.core.i18n.format.handler.inflection
import org.roldy.core.i18n.format.handler.selection
import org.roldy.core.i18n.format.handler.simple

typealias ArgumentResolver = (CompositeLocalizedString.Selector) -> String

operator fun LocalizedString.invoke(delegate: ArgumentResolver? = null) =
    LocalizedStringProxy(this, delegate)

operator fun LocalizedString.invoke(vararg suffixes: String) =
    (arrayOf(key) + suffixes).joinToString("-")

data class LocalizedStringProxy(
    val localizedString: LocalizedString,
    val argumentDelegate: ArgumentResolver? = null
)

typealias StringProvider<T> = T.() -> LocalizedStringProxy

fun <T : ILocalizedContext> StringProvider<T>.translate(context: T): String =
    this(context).let { proxy ->
        when (val loc = proxy.localizedString.handler) {
            is Simple -> proxy.simple(context)
            is Selection -> proxy.selection(context, loc)
            is Inflection -> proxy.inflection(context, loc)
            is Composite -> proxy.composite(context)
        }
    }