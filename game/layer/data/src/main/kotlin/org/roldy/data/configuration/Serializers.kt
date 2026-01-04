package org.roldy.data.configuration

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ClosedFloatingPointRangeSerializer : KSerializer<ClosedFloatingPointRange<Float>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("org.roldy.ClosedFloatingPointRange", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: ClosedFloatingPointRange<Float>
    ) {
        encoder.encodeString("${value.start}..${value.endInclusive}")
    }

    override fun deserialize(decoder: Decoder): ClosedFloatingPointRange<Float> {
        val decoded = decoder.decodeString()
        val comparator = FloatComparison.FloatComparator.entries.find {
            decoded.startsWith(it.char)
        }
        if (comparator != null) {
            val value = decoded.replaceFirst(comparator.char, "").toFloat()
            return when (comparator) {
                FloatComparison.FloatComparator.Lesser -> Float.MIN_VALUE..value
                FloatComparison.FloatComparator.Greater -> value..Float.MAX_VALUE
            }
        }
        val (start, endInclusive) = decoded.split("..").map { it.toFloat() }
        return start..endInclusive
    }

}

object G2DColorSerializer : KSerializer<Color> {
    // Serial names of descriptors should be unique, this is why we advise including app package in the name.
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("org.roldy.Color", PrimitiveKind.STRING)

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
        PrimitiveSerialDescriptor("org.roldy.FloatComparison", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): FloatComparison =
        decoder.decodeString().run {
            val comparator = FloatComparison.FloatComparator.entries.first {
                startsWith(it.char)
            }
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