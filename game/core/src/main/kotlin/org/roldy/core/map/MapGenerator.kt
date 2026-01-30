package org.roldy.core.map

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import org.roldy.core.IVector2Int
import org.roldy.core.SimplexNoise
import org.roldy.core.Vector2Int
import org.roldy.core.biome.BiomeData
import org.roldy.core.configuration.HeightData
import kotlin.math.abs

/**
 * flatRegionAmount = 0.3f More mountains, less plains
 * flatRegionAmount = 0.7f More plains, less mountains
 * flatHeight = 0.1f Flatter plains
 * flatHeight = 0.3f Rolling hills in plains
 * mountainHeight = 0.7f Lower mountains
 */
class MapGenerator(
    val mapData: MapData,
    val biomesData: List<BiomeData>,
    // Elevation settings
    val elevationOctaves: Int = 8,
    val elevationLatitude: Float = .3f,
    val elevationFrequency: Float = 0.0008f,

    // Simple terrain variety
    val flatRegionAmount: Float = 0.69f,  // 0-1: how much of map is flat
    val mountainHeight: Float = 2f,       // How tall mountains are
    val flatHeight: Float = 0.1f,         // Base height of flat regions

    // Temperature/moisture settings
    val temperatureOctaves: Int = 4,
    val temperatureLatitude: Float = 0.95f,
    val temperatureFrequency: Float = 0.05f,
    val moistureOctaves: Int = 3,
    val moistureLatitude: Float = 0f,
    val moistureFrequency: Float = 0.003f
) {
    private val temperatureNoise = SimplexNoise(mapData.seed)
    private val moistureNoise = SimplexNoise(mapData.seed + 1)
    private val elevationNoise = SimplexNoise(mapData.seed + 2)
    private val regionNoise = SimplexNoise(mapData.seed + 3)

    fun generate(): MapTerrainData {
        val noiseData = mutableMapOf<IVector2Int, NoiseData>()

        // First pass: raw elevation
        var minElevation = Float.MAX_VALUE
        var maxElevation = Float.MIN_VALUE
        val rawElevations = mutableMapOf<Vector2Int, Float>()

        for (x in 0 until mapData.size.width) {
            for (y in 0 until mapData.size.height) {
                val pos = Vector2Int(x, y)
                val elevation = getElevationRaw(x, y)
                rawElevations[pos] = elevation

                if (elevation < minElevation) minElevation = elevation
                if (elevation > maxElevation) maxElevation = elevation
            }
        }

        val range = maxElevation - minElevation

        // Second pass: normalize and shape
        for (x in 0 until mapData.size.width) {
            for (y in 0 until mapData.size.height) {
                val pos = Vector2Int(x, y)
                val rawElevation = rawElevations[pos]!!

                var elevation = if (range > 0) {
                    (rawElevation - minElevation) / range
                } else {
                    0.5f
                }

                // Apply region blending (flat vs mountain)
                elevation = blendRegions(x, y, elevation)

                val temperature = getTemperature(x, y)
                val moisture = getMoisture(x, y)

                noiseData[pos] = NoiseData(x, y, elevation, temperature, moisture)
            }
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

        val e = data.elevation.coerceIn(0f, 1f)  // Note: you have e=1.073 in debug!
        val t = data.temperature.coerceIn(0f, 1f)
        val m = data.moisture.coerceIn(0f, 1f)

        var matchedBiome: String?

        for (biome in biomesData) {
            if (e < biome.elevation.start || e > biome.elevation.endInclusive) continue
            if (t < biome.temperature.start || t > biome.temperature.endInclusive) continue
            if (m < biome.moisture.start || m > biome.moisture.endInclusive) continue

            matchedBiome = biome.type.name

            for (terrain in biome.terrains) {
                val eMin = maxOf(biome.elevation.start, terrain.elevation.start)
                val eMax = minOf(biome.elevation.endInclusive, terrain.elevation.endInclusive)
                val tMin = maxOf(biome.temperature.start, terrain.temperature.start)
                val tMax = minOf(biome.temperature.endInclusive, terrain.temperature.endInclusive)
                val mMin = maxOf(biome.moisture.start, terrain.moisture.start)
                val mMax = minOf(biome.moisture.endInclusive, terrain.moisture.endInclusive)

                if (eMin > eMax || tMin > tMax || mMin > mMax) continue
                if (e < eMin || e > eMax) continue
                if (t < tMin || t > tMax) continue
                if (m < mMin || m > mMax) continue
                w[terrain.index] = 1f
                return w
            }
        }
        w[0] = 1f  // Fallback
        return w
    }

    private fun HeightData.isInRange(e: Float, t: Float, m: Float, edge: Float): Boolean {
        val eOk = e >= elevation.start - edge && e <= elevation.endInclusive + edge
        val tOk = t >= temperature.start - edge && t <= temperature.endInclusive + edge
        val mOk = m >= moisture.start - edge && m <= moisture.endInclusive + edge
        return eOk && tOk && mOk
    }

    private fun intersectRanges(
        biomeRange: ClosedFloatingPointRange<Float>,
        terrainRange: ClosedFloatingPointRange<Float>
    ): ClosedFloatingPointRange<Float> {
        val start = maxOf(biomeRange.start, terrainRange.start)
        val end = minOf(biomeRange.endInclusive, terrainRange.endInclusive)
        return if (start <= end) start..end else biomeRange // Fallback to biome if no intersection
    }

    private fun calculateScore(
        e: Float, t: Float, m: Float,
        eRange: ClosedFloatingPointRange<Float>,
        tRange: ClosedFloatingPointRange<Float>,
        mRange: ClosedFloatingPointRange<Float>
    ): Float {
        // Higher score = closer to center of range
        fun centerScore(value: Float, range: ClosedFloatingPointRange<Float>): Float {
            val rangeSize = range.endInclusive - range.start
            if (rangeSize < 0.001f) return 1f // Point range

            val center = (range.start + range.endInclusive) / 2f
            val distFromCenter = kotlin.math.abs(value - center)
            val maxDist = rangeSize / 2f

            return 1f - (distFromCenter / maxDist) * 0.5f // 0.5 to 1.0 range
        }

        return centerScore(e, eRange) * centerScore(t, tRange) * centerScore(m, mRange)
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
        var value = regionNoise(x * 0.001f, y * 0.001f)
        value += regionNoise(x * 0.002f, y * 0.002f) * 0.5f

        // Normalize to 0-1
        value = (value / 1.5f + 1f) / 2f

        // Adjust balance between flat and mountain
        // Shift the midpoint based on flatRegionAmount
        val threshold = flatRegionAmount

        // Smooth transition
        return smoothstep(threshold - 0.15f, threshold + 0.15f, value)
    }

    private fun getElevationRaw(x: Int, y: Int): Float {
        var total = 0.0f
        var frequency = elevationFrequency
        var amplitude = 1f

        repeat(elevationOctaves) {
            total += elevationNoise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        if (elevationLatitude > 0.0f) {
            val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
            return latitudeFactor * elevationLatitude + total * (1.0f - elevationLatitude)
        }

        return total
    }

    private fun getTemperature(x: Int, y: Int): Float {
        val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))

        var noiseTotal = 0.0f
        var frequency = temperatureFrequency
        var amplitude = 1f
        var maxAmplitude = 0f

        repeat(temperatureOctaves) {
            noiseTotal += temperatureNoise(x * frequency, y * frequency) * amplitude
            maxAmplitude += amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        val normalizedNoise = (noiseTotal / maxAmplitude + 1f) / 2f
        return latitudeFactor * temperatureLatitude + normalizedNoise * (1.0f - temperatureLatitude)
    }

    private fun getMoisture(x: Int, y: Int): Float {
        var total = 0.0f
        var amplitude = 1f
        var frequency = moistureFrequency
        var maxAmplitude = 0f

        repeat(moistureOctaves) {
            total += moistureNoise(x * frequency, y * frequency) * amplitude
            maxAmplitude += amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        val normalizedNoise = (total / maxAmplitude + 1f) / 2f

        if (moistureLatitude > 0.0f) {
            val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
            return latitudeFactor * moistureLatitude + normalizedNoise * (1.0f - moistureLatitude)
        }

        return normalizedNoise
    }

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