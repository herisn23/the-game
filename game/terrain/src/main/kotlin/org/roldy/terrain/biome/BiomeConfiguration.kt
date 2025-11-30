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
        PrimitiveSerialDescriptor("org.roldy.terrain.Range", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Color =
        decoder.decodeString().let(Color::valueOf)

    override fun serialize(
        encoder: Encoder,
        value: Color
    ) {
        encoder.encodeString(value.toString())

    }
}

const val maxValue = 2f

@Serializable
data class BiomeData(
    val name: String,
    val darker: Boolean = true,
    override val elevation: Float = maxValue,
    override val temperature: Float = maxValue,
    override val moisture: Float = maxValue,
    val terrains: List<TerrainData>
) : HeightData {

    @Serializable
    data class TerrainData(
        val name: String,
        val color: String,
        override val elevation: Float = maxValue,
        override val temperature: Float = maxValue,
        override val moisture: Float = maxValue
    ) : HeightData
}

@Serializable
data class BiomesConfiguration(
    val biomes: List<BiomeData>,
    val settings: BiomesSettings
)

@Serializable
data class BiomesSettings(
    val color: Map<String, @Serializable(G2DColorSerializer::class) Color>
)