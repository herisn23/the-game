package org.roldy.core.renderer

interface Sortable {
    val zIndex: Float
    val layer:Int

    companion object {
        const val LAYER_1:Int = 0
        const val LAYER_2:Int = 1
    }
}
