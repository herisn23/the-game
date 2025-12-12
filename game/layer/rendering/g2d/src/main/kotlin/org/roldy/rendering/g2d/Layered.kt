package org.roldy.rendering.g2d

interface Layered : Sortable {
    val layer: Int

    companion object {
        const val LAYER_1: Int = 0
        const val LAYER_2: Int = 1
        const val LAYER_3: Int = 2
        const val LAYER_4: Int = 4
    }
}