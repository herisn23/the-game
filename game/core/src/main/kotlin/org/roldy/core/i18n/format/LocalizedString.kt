package org.roldy.core.i18n.format

import kotlinx.serialization.Serializable


@Serializable
data class LocalizedString(
    val key: String,
    val handler: LocalizedStringHandler = Simple
)