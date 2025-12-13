package org.roldy.core.utils


infix fun String.repeat(count: Int) =
    (0..count).joinToString("") { this }