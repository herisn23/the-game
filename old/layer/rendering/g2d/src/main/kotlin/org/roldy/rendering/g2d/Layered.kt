package org.roldy.rendering.g2d

interface Layered : Sortable {
    val layer: Int

    companion object {
        // CLAIMS

        const val LAYER_0: Int = -1

        // ROADS
        const val LAYER_1: Int = 0

        // Other objects which should be rendered by z-index
        const val LAYER_2: Int = 1

        // Objects which should dominate over all other objects to avoid overlapping
        const val LAYER_3: Int = 2

        // Should never be used, maybe should be player instead of layer 4?
        const val LAYER_4: Int = 3

        //PLAYER
        const val LAYER_5: Int = 4
    }
}