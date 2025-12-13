package org.roldy.data.configuration.biome

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable
import org.roldy.data.configuration.FloatComparison
import org.roldy.data.configuration.FloatComparisonSerializer
import org.roldy.data.configuration.G2DColorSerializer
import org.roldy.data.configuration.HeightData

const val maxValue = 10f

@Serializable
data class BiomeData(
    val name: String,
    val darker: Boolean = true,
    @Serializable(FloatComparisonSerializer::class)
    override val elevation: FloatComparison = FloatComparison(
        maxValue
    ),
    @Serializable(FloatComparisonSerializer::class)
    override val temperature: FloatComparison = FloatComparison(
        maxValue
    ),
    @Serializable(FloatComparisonSerializer::class)
    override val moisture: FloatComparison = FloatComparison(
        maxValue
    ),
    @Serializable(G2DColorSerializer::class)
    val color: Color,
    val walkCost: Float,
    val terrains: List<TerrainData>,
    val mountains: List<MountainData> = emptyList()
) : HeightData {

    @Serializable
    data class TerrainData(
        val name: String,
        @Serializable(FloatComparisonSerializer::class)
        override val elevation: FloatComparison = FloatComparison(
            maxValue
        ),
        @Serializable(FloatComparisonSerializer::class)
        override val temperature: FloatComparison = FloatComparison(
            maxValue
        ),
        @Serializable(FloatComparisonSerializer::class)
        override val moisture: FloatComparison = FloatComparison(
            maxValue
        )
    ) : HeightData
    @Serializable
    data class MountainData(
        val name: String,
        @Serializable(FloatComparisonSerializer::class)
        val elevation: FloatComparison = FloatComparison(
            0.876f,
            FloatComparison.FloatComparator.Greater
        ),
    )
}