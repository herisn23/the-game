package org.roldy.core.renderer

interface Layered : Sortable {
    val layer: Int

    companion object {
        const val LAYER_1: Int = 0
        const val LAYER_2: Int = 1
    }
}