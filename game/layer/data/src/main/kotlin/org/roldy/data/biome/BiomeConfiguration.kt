package org.roldy.data.biome

import kotlinx.serialization.Serializable


@Serializable
data class BiomesConfiguration(
    val biomes: List<BiomeData>
)