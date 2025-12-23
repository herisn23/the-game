package org.roldy.core.i18n

@I18NDsl
fun t(closure: TextKeys.() -> I18N.Key): () -> I18N.Key =
    { TextKeys.closure() }

