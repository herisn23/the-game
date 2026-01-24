package org.roldy.data.configuration.biome

import kotlinx.serialization.Serializable


@Serializable
data class BiomesConfiguration(
    val biomes: List<BiomeData>
)