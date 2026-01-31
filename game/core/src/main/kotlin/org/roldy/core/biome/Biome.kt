package org.roldy.core.biome

class Biome(
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

fun BiomesConfiguration.toBiomes() =
    biomes.map {
        Biome(
            it
        )
    }