package org.roldy.terrain.biome

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object G2DColorSerializer : KSerializer<Color> {
    // Serial names of descriptors should be unique, this is why we advise including app package in the name.
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("org.roldy.terrain.Color", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Color =
        decoder.decodeString().let(Color::valueOf)

    override fun serialize(
        encoder: Encoder,
        value: Color
    ) {
        encoder.encodeString(value.toString())

    }
}

object FloatComparisonSerializer : KSerializer<FloatComparison> {
    // Serial names of descriptors should be unique, this is why we advise including app package in the name.
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("org.roldy.terrain.Range", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): FloatComparison =
        decoder.decodeString().run {
            val comparator = FloatComparison.FloatComparator.entries.find {
                startsWith(it.char)
            } ?: FloatComparison.FloatComparator.Lesser
            val value = replaceFirst(comparator.char, "").toFloat()
            FloatComparison(value, comparator)
        }

    override fun serialize(
        encoder: Encoder,
        value: FloatComparison
    ) {
        encoder.encodeString("${value.equality.char}${value.value}")
    }
}

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

@Serializable
data class BiomesConfiguration(
    val biomes: List<BiomeData>
)