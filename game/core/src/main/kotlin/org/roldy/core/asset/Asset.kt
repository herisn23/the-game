package org.roldy.core.asset

interface Asset<T> {
    fun get(): T
}