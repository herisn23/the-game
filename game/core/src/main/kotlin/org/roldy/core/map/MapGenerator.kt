package org.roldy.core.map

import org.roldy.core.SimplexNoise
import org.roldy.core.Vector2Int
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

    fun generate(): Map<Vector2Int, NoiseData> {
        val terrainData = mutableMapOf<Vector2Int, NoiseData>()

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

                terrainData[pos] = NoiseData(x, y, elevation, temperature, moisture)
            }
        }

        return terrainData
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