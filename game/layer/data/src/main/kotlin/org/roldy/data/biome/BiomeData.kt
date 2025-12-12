package org.roldy.data.biome

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable

const val maxValue = 10f

@Serializable
data class BiomeData(
    val name: String,
    val darker: Boolean = true,
    @Serializable(FloatComparisonSerializer::class)
    override val elevation: FloatComparison = FloatComparison(maxValue),
    @Serializable(FloatComparisonSerializer::class)
    override val temperature: FloatComparison = FloatComparison(maxValue),
    @Serializable(FloatComparisonSerializer::class)
    override val moisture: FloatComparison = FloatComparison(maxValue),
    @Serializable(G2DColorSerializer::class)
    val color: Color,
    val walkCost: Float,
    val terrains: List<TerrainData>
) : HeightData {

    @Serializable
    data class TerrainData(
        val name: String,
        @Serializable(FloatComparisonSerializer::class)
        override val elevation: FloatComparison = FloatComparison(maxValue),
        @Serializable(FloatComparisonSerializer::class)
        override val temperature: FloatComparison = FloatComparison(maxValue),
        @Serializable(FloatComparisonSerializer::class)
        override val moisture: FloatComparison = FloatComparison(maxValue),
    ) : HeightData
}