package org.roldy.rendering.g2d.gui.i18n

import org.roldy.core.i18n.I18N
import org.roldy.rendering.g2d.gui.KContext

interface I18NContext: KContext {
    val i18n: I18N
}