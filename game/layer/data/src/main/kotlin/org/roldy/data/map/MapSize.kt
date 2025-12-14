package org.roldy.data.map

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.roldy.core.x
import kotlin.math.ceil

@Serializable
sealed interface MapSize {
    val width: Int
    val height: Int

    val size get() = width
    val chunks: Int get() = ceil(width / 10f).toInt()
    val settlements: Int
    val max get() = width x height
    val min get() = 0 x 0

    @SerialName("debug")
    @Serializable
    object Debug : MapSize {
        override val width: Int = 30
        override val height: Int = width
        override val settlements: Int = 3
    }

    @SerialName("small")
    @Serializable
    object Small : MapSize {
        override val width: Int = 300
        override val height: Int = width
        override val settlements: Int = 10
    }

    @SerialName("medium")
    @Serializable
    object Medium : MapSize {
        override val width: Int = 600
        override val height: Int = width
        override val settlements: Int = 20
    }

    @SerialName("large")
    @Serializable
    object Large : MapSize {
        override val width: Int = 1000
        override val height: Int = width
        override val settlements: Int = 40
    }

    @SerialName("extralarge")
    @Serializable
    object ExtraLarge : MapSize {
        override val width: Int = 2000
        override val height: Int = width
        override val settlements: Int = 80
    }

}