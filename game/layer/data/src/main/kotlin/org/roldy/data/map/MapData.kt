package org.roldy.data.map

data class MapData(
    val seed: Long,
    val size: MapSize,
    val tileSize: Int,
    val elevationScale: Float = 0.001f,   // Lower = smoother elevation changes, default:0.001f
    val moistureScale: Float = 0.003f,    // Lower = larger moisture zones, default:0.003f
    val temperatureScale: Float = 0.05f    // Lower = larger temperature zones, default:0.05f
)