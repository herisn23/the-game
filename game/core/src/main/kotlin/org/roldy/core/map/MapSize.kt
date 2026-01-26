package org.roldy.core.map

import kotlinx.serialization.Serializable

@Serializable
class MapSize(
    val width: Int,
    val height: Int = width,
)