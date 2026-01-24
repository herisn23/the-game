package org.roldy.core.ui

import org.roldy.core.i18n.I18N

interface I18NContext: UIContext {
    val i18n: I18N
}