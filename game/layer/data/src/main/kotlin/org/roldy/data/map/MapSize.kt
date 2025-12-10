package org.roldy.data.map

import kotlin.math.ceil

interface MapSize {
    val width: Int
    val height: Int

    val size get() = width
    val chunks: Int get() = ceil(width / 10f).toInt()
    val settlements: Int get() = chunks

    object Debug : MapSize {
        override val width: Int = 30
        override val height: Int = width
        override val settlements: Int = 10
    }

    object Small : MapSize {
        override val width: Int = 300
        override val height: Int = width
    }

    object Medium : MapSize {
        override val width: Int = 600
        override val height: Int = width
    }

    object Large : MapSize {
        override val width: Int = 1000
        override val height: Int = width
    }

    object ExtraLarge : MapSize {
        override val width: Int = 2000
        override val height: Int = width
    }

}