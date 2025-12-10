package org.roldy.gameplay.world.generator

import org.roldy.core.Vector2Int
import org.roldy.data.map.MapData
import org.roldy.data.map.NoiseData
import org.roldy.gameplay.world.SimplexNoise
import kotlin.math.abs

class ProceduralMapGenerator(
    val mapData: MapData,
    val elevationOctaves: Int = 4,//4
    val moistureOctaves: Int = 3,//3
    val temperatureLatitude: Float = 0.85f,//0.85f
) {
    private val temperatureNoise = SimplexNoise(mapData.seed)
    private val moistureNoise = SimplexNoise(mapData.seed + 1)
    private val elevationNoise = SimplexNoise(mapData.seed + 2)


    /**
     * Generates the noise data
     */
    fun generate(): Map<Vector2Int, NoiseData> {
        val terranData = mutableMapOf<Vector2Int, NoiseData>()
        for (x in 0 until mapData.size.width) {
            for (y in 0 until mapData.size.height) {
                terranData[Vector2Int(x, y)] = generateNoiseData(x, y)
            }
        }
        return terranData
    }

    /**
     * Generates elevation noise with multiple octaves for natural-looking height variation
     */
    private fun getElevation(x: Int, y: Int): Float {
        var total = 0.0f
        var frequency = mapData.elevationScale
        var amplitude = 1f

        // Use 4 octaves for detailed elevation
        repeat(elevationOctaves) {
            total += elevationNoise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        return total
    }

    /**
     * Generates noise data for a specific position
     */
    private fun generateNoiseData(x: Int, y: Int): NoiseData {
        val elevation = getElevation(x, y)
        val temperature = getTemperature(x, y)
        val moisture = getMoisture(x, y)
        return NoiseData(x, y, elevation, temperature, moisture)
    }

    /**
     * Generates temperature noise influenced by latitude
     * Higher latitudes (further from equator) are colder
     */
    private fun getTemperature(x: Int, y: Int): Float {
        // Calculate latitude factor (0 at poles, 1 at equator)
        val latitudeFactor = 1.0f - (abs(y - mapData.size.width / 2.0f) / (mapData.size.height / 2.0f))

        // Add some noise variation to temperature
        val noise = temperatureNoise(x * 0.008f, y * 0.008f)

        // Combine latitude with noise (85% latitude, 15% noise)
        return latitudeFactor * temperatureLatitude + noise * mapData.temperatureScale
    }

    /**
     * Generates moisture noise with multiple octaves
     */
    private fun getMoisture(x: Int, y: Int): Float {
        var total = 0.0f
        var amplitude = 1f
        var frequency = mapData.moistureScale

        // Use 3 octaves for moisture variation
        repeat(moistureOctaves) {
            total += moistureNoise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        // Normalize to 0-1 range
        return (total + 1) / 2
    }
}