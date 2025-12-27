package org.roldy.core.utils

infix fun Boolean.nor(other: Boolean): Boolean = !(this || other)