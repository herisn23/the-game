package org.roldy.pawn.skeleton.attribute

import java.util.*

abstract class NamedAttribute {
    abstract val name: String
    val capitalizedName by lazy {
        name.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }
}