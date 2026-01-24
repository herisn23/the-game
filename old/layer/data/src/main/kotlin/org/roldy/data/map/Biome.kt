package org.roldy.data.map

import org.roldy.data.configuration.biome.BiomeData

data class Biome(
    val data: BiomeData
) {
    val terrains by lazy {
        data.terrains.map {
            Terrain(this, it)
        }
    }
}

class Terrain(
    val biome: Biome,
    val data: BiomeData.TerrainData
)