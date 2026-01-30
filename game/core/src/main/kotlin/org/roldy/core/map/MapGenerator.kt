package org.roldy.core.map

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import org.roldy.core.IVector2Int
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
            noiseData.generateSplatMaps(),
            noiseData
        )
    }

    private fun Map<IVector2Int, NoiseData>.generateSplatMaps(): List<Texture> {
        val width = keys.maxOf { it.x } + 1
        val height = keys.maxOf { it.y } + 1

        val splatmaps = Array(7) {
            Pixmap(width, height, Pixmap.Format.RGBA8888).apply {
                // CRITICAL: Disable blending so alpha=0 pixels are written!
                blending = Pixmap.Blending.None
            }
        }

        for ((pos, data) in this) {
            val weights = chooseWeights(data)

            val sum = weights.sum()
            val w = if (sum > 0f) {
                weights.map { it / sum }
            } else {
                MutableList(28) { 0f }.apply { this[0] = 1f }
            }

            for (i in 0 until 7) {
                val r = (w[i * 4 + 0].toFloat() * 255f).toInt().coerceIn(0, 255)
                val g = (w[i * 4 + 1].toFloat() * 255f).toInt().coerceIn(0, 255)
                val b = (w[i * 4 + 2].toFloat() * 255f).toInt().coerceIn(0, 255)
                val a = (w[i * 4 + 3].toFloat() * 255f).toInt().coerceIn(0, 255)

                val color = (r shl 24) or (g shl 16) or (b shl 8) or a
                splatmaps[i].drawPixel(pos.x, pos.y, color)
            }
        }

        val textures = splatmaps.map { pixmap ->
            val tex = Texture(pixmap)
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            pixmap.dispose()
            tex
        }

        return textures
    }

    private fun chooseWeights(data: NoiseData): FloatArray {
        val w = FloatArray(28) { 0f }

        val e = data.elevation
        val t = data.temperature
        val m = data.moisture

        fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)

        fun transition(value: Float, start: Float, end: Float): Float {
            return smoothstep(((value - start) / (end - start)).coerceIn(0f, 1f))
        }

        // Elevation zones
        val isBeach = 1f - transition(e, 0f, 0.15f)
        val isLowland = transition(e, 0.1f, 0.2f) * (1f - transition(e, 0.35f, 0.45f))
        val isHills = transition(e, 0.35f, 0.45f) * (1f - transition(e, 0.6f, 0.7f))
        val isMountain = transition(e, 0.6f, 0.7f) * (1f - transition(e, 0.85f, 0.92f))
        val isPeak = transition(e, 0.85f, 0.92f)

        // Moisture split
        val isDry = 1f - transition(m, 0.4f, 0.6f)
        val isWet = transition(m, 0.4f, 0.6f)

        // Temperature split
        val isCold = 1f - transition(t, 0.4f, 0.6f)
        val isWarm = transition(t, 0.4f, 0.6f)

        // ========== ASSIGN TEXTURES ==========

        // Beach (0-3)
        w[0] = isBeach * isDry                // sand
        w[1] = isBeach * isWet                // wet sand / mud

        // Lowland (4-7)
        w[4] = isLowland * isWet              // grass
        w[5] = isLowland * isDry * isWarm     // dirt
        w[6] = isLowland * isDry * isCold     // dry grass
        w[7] = isLowland * isWet * isWarm     // lush grass

        // Hills (8-11)
        w[8] = isHills * isWet                // forest floor
        w[9] = isHills * isDry                // rocky dirt
        w[10] = isHills * isWet * isCold      // moss
        w[11] = isHills * isDry * isWarm      // dry rocks

        // Mountain (12-15)
        w[12] = isMountain * isDry            // rock
        w[13] = isMountain * isWet            // wet rock
        w[14] = isMountain * isCold           // cold rock
        w[15] = isMountain * isWarm           // warm rock

        // Peak (16-19)
        w[16] = isPeak * isCold               // snow
        w[17] = isPeak * isWarm               // alpine rock
        w[18] = isPeak * isCold * isWet       // ice
        w[19] = isPeak * isWarm * isDry       // bare peak

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