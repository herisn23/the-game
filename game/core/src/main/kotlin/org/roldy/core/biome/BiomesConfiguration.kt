package org.roldy.core.biome

import kotlinx.serialization.Serializable


@Serializable
data class BiomesConfiguration(
    val biomes: List<BiomeData>
)