package org.roldy.core.i18n.format.handler

import org.roldy.core.i18n.format.ILocalizedContext
import org.roldy.core.i18n.format.LocalizedStringProxy
import org.roldy.core.i18n.get

fun LocalizedStringProxy.simple(context: ILocalizedContext) =
    context.messageSource[context.locale(), localizedString.key]