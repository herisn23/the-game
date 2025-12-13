package org.roldy.data.map

import org.roldy.core.x
import kotlin.math.ceil

interface MapSize {
    val width: Int
    val height: Int

    val size get() = width
    val chunks: Int get() = ceil(width / 10f).toInt()
    val settlements: Int
    val max get() = width x height
    val min get() = 0 x 0

    object Debug : MapSize {
        override val width: Int = 30
        override val height: Int = width
        override val settlements: Int = 3
    }

    object Small : MapSize {
        override val width: Int = 300
        override val height: Int = width
        override val settlements: Int = 10
    }

    object Medium : MapSize {
        override val width: Int = 600
        override val height: Int = width
        override val settlements: Int = 20
    }

    object Large : MapSize {
        override val width: Int = 1000
        override val height: Int = width
        override val settlements: Int = 40
    }

    object ExtraLarge : MapSize {
        override val width: Int = 2000
        override val height: Int = width
        override val settlements: Int = 80
    }

}