package org.roldy.core.i18n.format.handler

import org.roldy.core.i18n.format.ILocalizedContext
import org.roldy.core.i18n.format.LocalizedStringProxy
import org.roldy.core.i18n.format.Selection
import org.roldy.core.i18n.format.invoke
import org.roldy.core.i18n.get

fun LocalizedStringProxy.selection(
    context: ILocalizedContext,
    handler: Selection
) = context.messageSource[context.locale(), localizedString(
    context.selection()?.let { s -> handler.selections.find { s == it } } ?: "")]