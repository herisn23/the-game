package org.roldy.core.biome

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable
import org.roldy.core.configuration.ClosedFloatingPointRangeSerializer
import org.roldy.core.configuration.G2DColorSerializer
import org.roldy.core.configuration.HeightData

const val maxValue = Float.MAX_VALUE
const val minValue = -Float.MAX_VALUE

@Serializable
data class BiomeData(
    val type: BiomeType,
    val darker: Boolean = true,
    @Serializable(ClosedFloatingPointRangeSerializer::class)
    override val elevation: ClosedFloatingPointRange<Float> = minValue..maxValue,
    @Serializable(ClosedFloatingPointRangeSerializer::class)
    override val temperature: ClosedFloatingPointRange<Float> = minValue..maxValue,
    @Serializable(ClosedFloatingPointRangeSerializer::class)
    override val moisture: ClosedFloatingPointRange<Float> = minValue..maxValue,
    @Serializable(ClosedFloatingPointRangeSerializer::class)
    override val climate: ClosedFloatingPointRange<Float> = minValue..maxValue,
    @Serializable(G2DColorSerializer::class)
    val color: Color,
    val walkCost: Float,
    val terrains: List<TerrainData>
) : HeightData {

    @Serializable
    data class TerrainData(
        val index: Int,
        val key: String = "",
        val groupKey: String = "",
        val walkCost: Float? = null,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val elevation: ClosedFloatingPointRange<Float> = minValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val temperature: ClosedFloatingPointRange<Float> = minValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val moisture: ClosedFloatingPointRange<Float> = minValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val climate: ClosedFloatingPointRange<Float> = minValue..maxValue,
    ) : HeightData

    @Serializable
    data class StaticSpawnData(
        val name: String,
        val spawnAt: String? = null,
        val spawnAtGroup: String? = null,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val elevation: ClosedFloatingPointRange<Float> = minValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val temperature: ClosedFloatingPointRange<Float> = minValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val moisture: ClosedFloatingPointRange<Float> = minValue..maxValue,
        @Serializable(ClosedFloatingPointRangeSerializer::class)
        override val climate: ClosedFloatingPointRange<Float> = minValue..maxValue,
        val minRadius: Int = 0,
        val spawnChance: Float = 1f,
        val walkCost: Float = 1f
    ) : HeightData
}