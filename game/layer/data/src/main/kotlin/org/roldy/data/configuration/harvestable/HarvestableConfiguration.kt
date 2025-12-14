package org.roldy.data.configuration.harvestable

import kotlinx.serialization.Serializable
import org.roldy.data.configuration.ClosedFloatingPointRangeSerializer
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.configuration.biome.maxValue
import org.roldy.data.mine.harvestable.Fiber
import org.roldy.data.mine.harvestable.Gem
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.data.mine.harvestable.Metal
import org.roldy.data.mine.harvestable.Wood

@Serializable
data class HarvestableConfiguration(
    val wood: Map<Wood, HarvestableData>,
    val metal: Map<Metal, HarvestableData>,
    val fiber: Map<Fiber, HarvestableData>,
    val gem: Map<Gem, HarvestableData>,
) {
    val rules: Map<Harvestable, HarvestableData> = wood + metal + fiber + gem

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