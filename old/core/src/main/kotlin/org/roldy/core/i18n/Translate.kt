package org.roldy.core.i18n

@I18NDsl
fun t(closure: Strings.() -> I18N.Key): () -> I18N.Key =
    { Strings.closure() }

