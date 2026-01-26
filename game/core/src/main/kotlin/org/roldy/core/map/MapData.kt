package org.roldy.core.map

import kotlinx.serialization.Serializable

@Serializable
data class MapData(
    val seed: Long,
    val size: MapSize
)