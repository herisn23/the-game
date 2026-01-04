package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.data.map.MapData
import org.roldy.data.map.NoiseData
import org.roldy.gp.world.SimplexNoise
import kotlin.math.abs

class ProceduralMapGenerator(
    val mapData: MapData,
    //elevaton settings
    val elevationOctaves: Int = 8,
    val elevationLatitude: Float = .3f,
    val elevationFrequency: Float = 0.0008f,   // Lower = smoother elevation changes, default:0.001f

    //temperature settings
    val temperatureOctaves: Int = 4,
    val temperatureLatitude: Float = 0.80f,
    val temperatureFrequency: Float = 0.05f,    // Lower = larger temperature zones, default:0.05f

    //moisture settings
    val moistureOctaves: Int = 3,
    val moistureLatitude: Float = 0f,
    val moistureFrequency: Float = 0.003f    // Lower = larger moisture zones, default:0.003f
) {
    private val temperatureNoise = SimplexNoise(mapData.seed)
    private val moistureNoise = SimplexNoise(mapData.seed + 1)
    private val elevationNoise = SimplexNoise(mapData.seed + 2)

    /**
     * Generates the noise data with normalized elevation
     */
    fun generate(): Map<Vector2Int, NoiseData> {
        val terranData = mutableMapOf<Vector2Int, NoiseData>()

        // First pass: generate raw elevation and find actual min/max
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

        // Calculate range for normalization
        val range = maxElevation - minElevation

        // Second pass: normalize elevation and generate final data
        for (x in 0 until mapData.size.width) {
            for (y in 0 until mapData.size.height) {
                val pos = Vector2Int(x, y)
                val rawElevation = rawElevations[pos]!!

                // Normalize to [0, 1] based on actual min/max
                val normalizedElevation = if (range > 0) {
                    (rawElevation - minElevation) / range
                } else {
                    0.5f // Fallback if somehow all values are the same
                }

                val temperature = getTemperature(x, y)
                val moisture = getMoisture(x, y)

                terranData[pos] = NoiseData(x, y, normalizedElevation, temperature, moisture)
            }
        }

        return terranData
    }

    /**
     * Generates raw elevation noise (before normalization)
     */
    private fun getElevationRaw(x: Int, y: Int): Float {
        var total = 0.0f
        var frequency = elevationFrequency
        var amplitude = 1f

        repeat(elevationOctaves) {
            total += elevationNoise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        // Optional latitude influence
        if (elevationLatitude > 0.0f) {
            val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
            return latitudeFactor * elevationLatitude + total * (1.0f - elevationLatitude)
        }

        return total
    }

    /**
     * Generates temperature noise with latitude influence and noise variation
     * Warmer at equator, colder at poles (controlled by temperatureLatitude parameter)
     */
    private fun getTemperature(x: Int, y: Int): Float {
        // Calculate latitude factor (0 at poles, 1 at equator)
        val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))

        // Generate multi-octave temperature noise for varied patterns
        var noiseTotal = 0.0f
        var frequency = temperatureFrequency
        var amplitude = 1f
        var maxAmplitude = 0f  // Track total amplitude for proper normalization

        repeat(temperatureOctaves) {
            noiseTotal += temperatureNoise(x * frequency, y * frequency) * amplitude
            maxAmplitude += amplitude  // Sum up amplitudes
            frequency *= 2
            amplitude *= 0.5f
        }

        // FIXED: Normalize by actual amplitude sum, not assuming [-1, 1]
        // First normalize to [-1, 1], then to [0, 1]
        val normalizedNoise = (noiseTotal / maxAmplitude + 1f) / 2f

        // Combine latitude with noise (warmer at equator, colder at poles)
        return latitudeFactor * temperatureLatitude + normalizedNoise * (1.0f - temperatureLatitude)
    }

    /**
     * Generates moisture noise with multiple octaves
     * Can optionally incorporate latitude influence (wetter at equator, drier at poles)
     */
    private fun getMoisture(x: Int, y: Int): Float {
        var total = 0.0f
        var amplitude = 1f
        var frequency = moistureFrequency
        var maxAmplitude = 0f  // Track total amplitude for proper normalization

        repeat(moistureOctaves) {
            total += moistureNoise(x * frequency, y * frequency) * amplitude
            maxAmplitude += amplitude  // Sum up amplitudes
            frequency *= 2
            amplitude *= 0.5f
        }

        // FIXED: Normalize by actual amplitude sum, not assuming [-1, 1]
        // First normalize to [-1, 1], then to [0, 1]
        val normalizedNoise = (total / maxAmplitude + 1f) / 2f

        // Optional latitude influence (higher moisture at equator)
        if (moistureLatitude > 0.0f) {
            val latitudeFactor = 1.0f - (abs(y - mapData.size.height / 2.0f) / (mapData.size.height / 2.0f))
            return latitudeFactor * moistureLatitude + normalizedNoise * (1.0f - moistureLatitude)
        }

        return normalizedNoise
    }
}