package org.roldy.data.configuration.biome

import kotlinx.serialization.Serializable
import org.roldy.data.configuration.biome.BiomeData


@Serializable
data class BiomesConfiguration(
    val biomes: List<BiomeData>
)