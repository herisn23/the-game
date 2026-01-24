package org.roldy.core.i18n.format

import org.roldy.core.i18n.MessageSource

interface ILocalizedContext {
    val messageSource: MessageSource
    val genus: () -> Genus?
    val selection: () -> String?
    val locale: () -> String
}