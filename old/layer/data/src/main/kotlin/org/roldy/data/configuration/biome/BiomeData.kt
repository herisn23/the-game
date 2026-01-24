package org.roldy.data.configuration.biome

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable
import org.roldy.data.configuration.ClosedFloatingPointRangeSerializer
import org.roldy.data.configuration.G2DColorSerializer
import org.roldy.data.configuration.HeightData

const val maxValue = 1f

@Serializable
data class BiomeData(
    val type: BiomeType,
    val darker: Boolean = true,
    @Serializable(ClosedFloatingPointRangeSerializer::class)
    override val elevation: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
    @Serializable(ClosedFloatingPointRangeSerializer::class)
    override val temperature: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
    @Serializable(ClosedFloatingPointRangeSerializer::class)
    override val moisture: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
    @Serializable(G2DColorSerializer::class)
    val color: Color,
    val walkCost: Float,
    val terrains: List<TerrainData>,
    val mountains: List<StaticSpawnData> = emptyList(),
    val trees: List<StaticSpawnData> = emptyList()
) : HeightData {

    @Serializable
    data class TerrainData(
        val name: String,
        val key: String = "",
        val groupKey: String = "",
        val walkCost: Float? = null,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val elevation: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val temperature: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val moisture: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
    ) : HeightData

    @Serializable
    data class StaticSpawnData(
        val name: String,
        val spawnAt: String? = null,
        val spawnAtGroup: String? = null,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val elevation: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val temperature: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val moisture: ClosedFloatingPointRange<Float> = -maxValue..maxValue,
        val minRadius: Int = 0,
        val spawnChance: Float = 1f,
        val walkCost: Float = 1f
    ) : HeightData
}