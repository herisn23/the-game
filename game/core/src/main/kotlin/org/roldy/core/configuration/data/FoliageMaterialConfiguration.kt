package org.roldy.core.configuration.data

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable
import org.roldy.core.configuration.G2DColorSerializer

@Serializable
data class FoliageMaterialConfiguration(
    val materials: List<FoliageConfiguration>
)

@Serializable
data class FoliageConfiguration(
    val id: String,
    val useColorNoise: Boolean,
    val smallNoiseFreq: Float = 0f,
    val largeNoiseFreq: Float = 0f,
    val leaves: LeavesConfiguration? = null,
    val trunk: TrunkConfiguration? = null,
)

@Serializable
data class LeavesConfiguration(
    val texture: String,
    val normal: String? = null,
    val metallic: Float = 0f,
    val smoothness: Float = 0f,
    val normalStrength: Float = 0f,
    val useFlatColor: Boolean = false,
    @Serializable(G2DColorSerializer::class)
    val baseColor: Color = Color.WHITE,
    @Serializable(G2DColorSerializer::class)
    val noiseColor: Color = Color.WHITE,
    @Serializable(G2DColorSerializer::class)
    val noiseLargeColor: Color = Color.WHITE
)

@Serializable
data class TrunkConfiguration(
    val texture: String,
    val normal: String? = null,
    val emissive: String? = null,
    val useFlatColor: Boolean = false,
    val metallic: Float = 0f,
    val smoothness: Float = 0f,
    val normalStrength: Float = 0f,
    @Serializable(G2DColorSerializer::class)
    val baseColor: Color = Color.WHITE,
    @Serializable(G2DColorSerializer::class)
    val noiseColor: Color = Color.WHITE,
    @Serializable(G2DColorSerializer::class)
    val emissiveColor: Color = Color.WHITE
)