package org.roldy.core

import java.util.*

abstract class NamedAttribute {
    abstract val name: String
    val capitalizedName by lazy {
        name.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }
}