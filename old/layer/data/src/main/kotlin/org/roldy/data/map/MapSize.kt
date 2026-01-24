package org.roldy.data.map

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.roldy.core.x
import kotlin.math.ceil

@Serializable
sealed interface MapSize {
    val width: Int
    val height: Int get() = calculateHeight()

    val chunks: Int get() = ceil(width / 10f).toInt()
    val max get() = width x height
    val min get() = 0 x 0
    val yCorrection get() = .75f

    /**
     * Calculates the corrected height for a hexagonal map to maintain a 1:1 aspect ratio rectangle.
     *
     * Hexagonal tiles have an inherent aspect ratio (height != width) depending on orientation.
     * This function adjusts the map height so the overall rendered map appears square.
     *
     * @return Adjusted height = width / yCorrection
     *
     * Where yCorrection is:
     * - ~0.866 (√3/2) for flat-top hexagons
     * - ~1.155 (2/√3) for pointy-top hexagons
     */
    fun calculateHeight() =
        (width / yCorrection).toInt()

    fun viewPortWidth(tileWidth: Int) = width * tileWidth.toFloat()
    fun viewPortHeight(tileHeight: Int) = width * tileHeight.toFloat()

    @SerialName("debug")
    @Serializable
    object Debug : MapSize {
        override val width: Int = 2
    }

    @SerialName("small")
    @Serializable
    object Small : MapSize {
        override val width: Int = 300
    }

    @SerialName("medium")
    @Serializable
    object Medium : MapSize {
        override val width: Int = 600
    }

    @SerialName("large")
    @Serializable
    object Large : MapSize {
        override val width: Int = 1000
    }

    @SerialName("extralarge")
    @Serializable
    object ExtraLarge : MapSize {
        override val width: Int = 2000
    }

}

fun main() {
    val w = 100f
    val c = .75f
    val h = w * c
    println(h)
}