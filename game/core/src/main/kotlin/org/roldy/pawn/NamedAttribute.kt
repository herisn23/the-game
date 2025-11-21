package org.roldy.pawn

import java.util.Locale

abstract class NamedAttribute {
    abstract val name: String
    val value by lazy {
        name.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }
}