package org.roldy.core.i18n.format.handler

import org.roldy.core.i18n.format.ILocalizedContext
import org.roldy.core.i18n.format.Inflection
import org.roldy.core.i18n.format.LocalizedStringProxy
import org.roldy.core.i18n.format.invoke
import org.roldy.core.i18n.get

fun LocalizedStringProxy.inflection(
    engine: ILocalizedContext,
    handler: Inflection
): String =
    when (handler.type) {
        is Inflection.Genus -> engine.messageSource[engine.locale(), localizedString(engine.genus()?.code?:"")]
    }