package org.roldy.core.map

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import org.roldy.core.IVector2Int
import org.roldy.core.SimplexNoise
import org.roldy.core.biome.Biome
import org.roldy.core.biome.BiomeData
import org.roldy.core.configuration.HeightData
import org.roldy.core.utils.repeat
import org.roldy.core.x
import kotlin.math.abs
import kotlin.random.Random

/**
 * flatRegionAmount = 0.3f More mountains, less plains
 * flatRegionAmount = 0.7f More plains, less mountains
 * flatHeight = 0.1f Flatter plains
 * flatHeight = 0.3f Rolling hills in plains
 * mountainHeight = 0.7f Lower mountains
 */
class MapGenerator(
    val mapData: MapData,
    val biomes: List<Biome>,
    // Elevation settings
    val elevation: HeightConfiguration = HeightConfiguration(9, 1f, .3f, 0.0002f),
    val temperature: HeightConfiguration = HeightConfiguration(9, 1f, 0.81f, 0.0008f),
    val moisture: HeightConfiguration = HeightConfiguration(9, 1f, 0.73f, 0.0008f),
    val climate: HeightConfiguration = HeightConfiguration(4, 1f, 0.33f, 0.001f),
    // Simple terrain variety
    val flatness: Float = 0.70f,  // 0-1: how much of map is flat
    val mountainHeight: Float = 1f,       // How tall mountains are
    val flatHeight: Float = 0.11f         // Base height of flat regions
) {
    private val seed = 1L//mapData.seed - 3
    private val noiseRandom = Random(seed)

    data class HeightConfiguration(
        val octaves: Int,
        val amplitude: Float,
        val latitude: Float,
        val frequency: Float
    )

    private val heightRegionNoise = SimplexNoise(noiseRandom.nextLong())

    private val elevationNoise = SimplexNoise(seed)
    private val temperatureNoise = SimplexNoise(noiseRandom.nextLong())
    private val moistureNoise = SimplexNoise(noiseRandom.nextLong())
    private val climateNoise = SimplexNoise(noiseRandom.nextLong())

    fun generate(): MapTerrainData {
        val noiseData = mutableMapOf<IVector2Int, NoiseData>()
        // Second pass: normalize and shape
        repeat(mapData.size.width, mapData.size.height) { x, y ->

            val elevation = elevation.getData(elevationNoise, x, y)
            val temperature = temperature.getData(temperatureNoise, x, y)
            val moisture = moisture.getData(moistureNoise, x, y)
            val climate = climate.getData(climateNoise, x, y)

            val blendElevation = blendRegions(x, y, elevation)
            noiseData[x x y] = NoiseData(x, y, blendElevation, temperature, moisture, climate)
        }
        return MapTerrainData(
            noiseData.generateSplatMaps(32),
            noiseData
        )
    }

    private fun Map<IVector2Int, NoiseData>.generateSplatMaps(materialCount: Int): List<Texture> {
        val texCount = 4 // textures count per splatmap, 4 textures, 8 splatmaps (4*8=32) total 32 textures
        val width = keys.maxOf { it.x } + 1
        val height = keys.maxOf { it.y } + 1

        val splatmaps = Array(materialCount / texCount) {//4 textures per splatmap
            Pixmap(width, height, Pixmap.Format.RGBA8888).apply {
                // CRITICAL: Disable blending so alpha=0 pixels are written!
                blending = Pixmap.Blending.None
            }
        }
        forEach { (pos, data) ->
            val weights = chooseWeights(data, materialCount)

            val sum = weights.sum()
            val w = if (sum > 0f) weights.map { it / sum } else weights.toList()

            splatmaps.forEachIndexed { i, pixmap ->
                val r = (w[i * texCount + 0] * 255f).toInt().coerceIn(0, 255)
                val g = (w[i * texCount + 1] * 255f).toInt().coerceIn(0, 255)
                val b = (w[i * texCount + 2] * 255f).toInt().coerceIn(0, 255)
                val a = (w[i * texCount + 3] * 255f).toInt().coerceIn(0, 255)

                val color = (r shl 24) or (g shl 16) or (b shl 8) or a
                pixmap.drawPixel(pos.x, pos.y, color)
            }
        }
        val textures = splatmaps.mapIndexed { i, pixmap ->
            Texture(pixmap).apply {
                setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                pixmap.dispose()
            }
        }
        return textures
    }

    private fun chooseWeights(data: NoiseData, materialCount: Int): FloatArray {
        val w = FloatArray(materialCount) { 0f }

        val e = data.elevation
        val t = data.temperature
        val m = data.moisture
        val c = data.climate
        fun <T : HeightData> range(
            biome: T,
            terrain: T,
            closedRange: T.() -> ClosedRange<Float>
        ): ClosedRange<Float> =
            maxOf(biome.closedRange().start, terrain.closedRange().start)..minOf(
                biome.closedRange().endInclusive,
                terrain.closedRange().endInclusive
            )

        val terrains = mutableListOf<BiomeData.TerrainData>()
        for (b in biomes) {
            val biome = b.data
            if (e !in biome.elevation) continue
            if (t !in biome.temperature) continue
            if (m !in biome.moisture) continue
            if (c !in biome.climate) continue
            for (terrain in biome.terrains) {

                val elevation = range(biome, terrain) { elevation }
                val moisture = range(biome, terrain) { moisture }
                val temperature = range(biome, terrain) { temperature }
                val climate = range(biome, terrain) { climate }

                if (e !in elevation) continue
                if (t !in temperature) continue
                if (m !in moisture) continue
                if (c !in climate) continue
                terrains.add(terrain)
                w[terrain.index] = 1f
                return w
            }
        }
        //test blending
        val mainTexture = terrains.first()
        val secondaryTexture = terrains.getOrNull(1)
        w[mainTexture.index] = 1f

        if (secondaryTexture != null) {
            w[mainTexture.index] = 0.8f
            w[secondaryTexture.index] = 0.2f
        }

        return w
    }

    /**
     * Blend between flat plains and mountainous terrain
     */
    private fun blendRegions(x: Int, y: Int, elevation: Float): Float {
        // Get region type (0 = flat, 1 = mountain)
        val regionValue = getRegionMask(x, y)

        // Flat region: compress elevation to low values with small hills
        val flatElevation = flatHeight + elevation * 0.15f

        // Mountain region: full elevation range
        val mountainElevation = elevation * mountainHeight

        // Smooth blend between regions
        return lerp(flatElevation, mountainElevation, regionValue)
    }

    /**
     * Smooth region mask: 0 = flat plains, 1 = mountains
     */
    private fun getRegionMask(x: Int, y: Int): Float {
        // Large scale noise for regions
        var value = heightRegionNoise(x * 0.001f, y * 0.001f)
        value += heightRegionNoise(x * 0.002f, y * 0.002f) * 0.5f

        // Normalize to 0-1
        value = (value / 1.5f + 1f) / 2f

        // Adjust balance between flat and mountain
        // Shift the midpoint based on flatRegionAmount
        val threshold = flatness

        // Smooth transition
        return smoothstep(threshold - 0.15f, threshold + 0.15f, value)
    }

    private fun HeightConfiguration.getData(noise: SimplexNoise, x: Int, y: Int): Float {
        var total = 0.0f
        var frequency = frequency
        var amplitude = amplitude

        repeat(octaves) {
            total += noise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        if (latitude > 0.0f) {
            val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
            return latitudeFactor * latitude + total * (1.0f - latitude)
        }

        return total
    }
//
//    private fun getTemperature2(x: Int, y: Int): Float {
//        var total = 0.0f
//        var frequency = temperatureFrequency
//        var amplitude = 1f
//
//        repeat(temperatureOctaves) {
//            total += temperatureNoise(x * frequency, y * frequency) * amplitude
//            frequency *= 2
//            amplitude *= 0.5f
//        }
//
//        if (temperatureLatitude > 0.0f) {
//            val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
//            return latitudeFactor * temperatureLatitude + total * (1.0f - temperatureLatitude)
//        }
//
//        return total
//    }

//    private fun getTemperature(x: Int, y: Int): Float {
//        val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
//
//        var noiseTotal = 0.0f
//        var frequency = temperatureFrequency
//        var amplitude = 1f
//        var maxAmplitude = 0f
//
//        repeat(temperatureOctaves) {
//            noiseTotal += temperatureNoise(x * frequency, y * frequency) * amplitude
//            maxAmplitude += amplitude
//            frequency *= 2
//            amplitude *= 0.5f
//        }
//
//        val normalizedNoise = (noiseTotal / maxAmplitude + 1f) / 2f
//        return latitudeFactor * temperatureLatitude + normalizedNoise * (1.0f - temperatureLatitude)
//    }
//
//    private fun getMoisture(x: Int, y: Int): Float {
//        var total = 0.0f
//        var amplitude = 1f
//        var frequency = moistureFrequency
//        var maxAmplitude = 0f
//
//        repeat(moistureOctaves) {
//            total += moistureNoise(x * frequency, y * frequency) * amplitude
//            maxAmplitude += amplitude
//            frequency *= 2
//            amplitude *= 0.5f
//        }
//
//        val normalizedNoise = (total / maxAmplitude + 1f) / 2f
//
//        if (moistureLatitude > 0.0f) {
//            val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
//            return latitudeFactor * moistureLatitude + normalizedNoise * (1.0f - moistureLatitude)
//        }
//
//        return normalizedNoise
//    }

    private fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }

    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * t
    }
}

class MapTerrainData(
    val splatMaps: List<Texture>,
    val noiseData: Map<IVector2Int, NoiseData>
)