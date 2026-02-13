package org.roldy.core.configuration.data

import kotlinx.serialization.Serializable

@Serializable
data class BiomesConfiguration(
    val biomes: List<BiomeData>
)