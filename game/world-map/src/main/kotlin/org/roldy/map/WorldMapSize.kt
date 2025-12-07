package org.roldy.map

import kotlin.math.ceil

interface WorldMapSize {
    val size: Int
    val chunks: Int get() = ceil(size / 10f).toInt()
    val settlements: Int get() = chunks

    val elevationScale: Float get() = 0.001f    // Lower = smoother elevation changes, default:0.001f
    val moistureScale: Float get() = 0.003f     // Lower = larger moisture zones, default:0.003f
    val temperatureScale: Float get() = 0.05f    // Lower = larger temperature zones, default:0.05f

    object Debug : WorldMapSize {
        override val size: Int = 10
    }

    object Small : WorldMapSize {
        override val size: Int = 300
        override val moistureScale: Float = 0.005f
        override val temperatureScale: Float = 0.04f
    }

    object Medium : WorldMapSize {
        override val size: Int = 600
    }

    object Large : WorldMapSize {
        override val size: Int = 1000
    }

    object ExtraLarge : WorldMapSize {
        override val size: Int = 2000
    }

}