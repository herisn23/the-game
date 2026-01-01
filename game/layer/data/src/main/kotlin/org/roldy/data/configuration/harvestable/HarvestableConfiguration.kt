package org.roldy.data.configuration.harvestable

import kotlinx.serialization.Serializable
import org.roldy.data.configuration.ClosedFloatingPointRangeSerializer
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.configuration.biome.maxValue
import org.roldy.data.mine.harvestable.*

@Serializable
data class HarvestableConfiguration(
    val wood: Map<Wood, HarvestableData>,
    val ore: Map<Ore, HarvestableData>,
    val fiber: Map<Fiber, HarvestableData>,
    val gem: Map<Gem, HarvestableData>,
    val leather: Map<Leather, HarvestableData>
) {
    val rules: Map<Harvestable, HarvestableData> = wood + ore + fiber + gem + leather

    @Serializable
    data class HarvestableData(
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        val elevation: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        val moisture: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        val temperature: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        val biomes: List<BiomeType> = BiomeType.entries
    )
}