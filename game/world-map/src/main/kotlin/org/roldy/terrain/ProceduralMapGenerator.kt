package org.roldy.terrain

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.x
import org.roldy.terrain.biome.*
import kotlin.math.abs

/**
 * Procedural map generator with biome support and tile transitions.
 *
 * This generator creates natural-looking terrain by:
 * - Using multiple noise layers (elevation, temperature, moisture)
 * - Selecting appropriate biomes and terrains based on environmental conditions
 * - Adding smooth transitions between different terrain types
 *
 * @param seed Random seed for reproducible generation
 * @param width Map width in tiles
 * @param height Map height in tiles
 * @param tileSize Size of each tile in pixels
 * @param elevationScale Scale factor for elevation noise (lower = smoother elevation changes)
 * @param moistureScale Scale factor for moisture noise (lower = larger moisture zones)
 * @param temperatureScale Scale factor for temperature noise (lower = larger temperature zones)
 * @param enableTransitions Whether to enable tile transitions for smoother terrain blending
 * @param debugMode Whether to store debug information (position, terrain name) in tile properties
 */

data class TileData(
    val terrain: Terrain,
    val noiseData: ProceduralMapGenerator.NoiseData
)

class ProceduralMapGenerator(
    val seed: Long,
    val width: Int,
    val height: Int,
    val tileSize: Int,
    val elevationScale: Float,
    val moistureScale: Float,
    val temperatureScale: Float,
    val elevationOctaves:Int = 4,//4
    val moistureOctaves:Int = 3,//3
    val temperatureLatitude:Float = 0.85f//0.85f
) {

    private val temperatureNoise = SimplexNoise(seed)
    private val moistureNoise = SimplexNoise(seed + 1)
    private val elevationNoise = SimplexNoise(seed + 2)

    // Cache for terrain at each position
    private val terrainCache = mutableMapOf<Vector2Int, TileData>()
    private val fallbackTerrain = createFallbackTerrain()

    /**
     * Generates a complete TiledMap with biomes and optional transitions
     */
    fun generate(biomesConfiguration:String): TiledMap {
        val tiledMap = TiledMap()
        val biomes = loadBiomes(tileSize, biomesConfiguration)
        // Generate base terrain layer
        tiledMap.layers.add(generateBaseLayer(biomes))

        // Generate colors layer
        tiledMap.layers.add(generateBiomeLayer())
        return tiledMap
    }

    val terrainData: Map<Vector2Int, TileData> get() = terrainCache

    private fun generateBiomeLayer(): TiledMapTileLayer {
        val layer = TiledMapTileLayer(width, height, tileSize, tileSize)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val terrain = terrainCache.getValue(x x y)
                val cell = TiledMapTileLayer.Cell().apply {
                    tile = StaticTiledMapTile(terrain.terrain.biome.color)
                }
                layer.setCell(x, y, cell)
            }
        }
        return layer
    }

    /**
     * Generates the base terrain layer without transitions
     */
    private fun generateBaseLayer(biomes: List<Biome>): TiledMapTileLayer {
        val layer = TiledMapTileLayer(width, height, tileSize, tileSize)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val noiseData = generateNoiseData(x, y)
                val terrain = findTerrainForNoise(biomes, noiseData)

                // Cache the terrain for transition calculations
                terrainCache[Vector2Int(x, y)] = TileData(
                    terrain,
                    noiseData
                )

                // Create cell with the selected terrain
                val cell = TiledMapTileLayer.Cell().apply {
                    tile = StaticTiledMapTile(terrain.region)
                }
                layer.setCell(x, y, cell)
            }
        }

        return layer
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
     * Finds the appropriate terrain for the given noise data
     */
    private fun findTerrainForNoise(biomes: List<Biome>, noiseData: NoiseData): Terrain {
        // Find matching biome
        val biome = biomes.find { biome ->
            biome.data.elevation.match(noiseData.elevation) &&
                    biome.data.temperature.match(noiseData.temperature) &&
                    biome.data.moisture.match(noiseData.moisture)
        }

        // Find matching terrain within biome
        val terrain = biome?.terrains?.find { terrain ->
            terrain.data.elevation.match(noiseData.elevation) &&
                    terrain.data.temperature.match(noiseData.temperature) &&
                    terrain.data.moisture.match(noiseData.moisture)
        }
        return terrain ?: fallbackTerrain.also {
            logger.debug { "No terrain for ${biome?.data?.name} for $noiseData" }
        }
    }

    /**
     * Generates elevation noise with multiple octaves for natural-looking height variation
     */
    private fun getElevation(x: Int, y: Int): Float {
        var total = 0.0f
        var frequency = elevationScale
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
     * Generates temperature noise influenced by latitude
     * Higher latitudes (further from equator) are colder
     */
    private fun getTemperature(x: Int, y: Int): Float {
        // Calculate latitude factor (0 at poles, 1 at equator)
        val latitudeFactor = 1.0f - (abs(y - height / 2.0f) / (height / 2.0f))

        // Add some noise variation to temperature
        val noise = temperatureNoise(x * 0.008f, y * 0.008f)

        // Combine latitude with noise (85% latitude, 15% noise)
        return latitudeFactor * temperatureLatitude + noise * temperatureScale
    }

    /**
     * Generates moisture noise with multiple octaves
     */
    private fun getMoisture(x: Int, y: Int): Float {
        var total = 0.0f
        var amplitude = 1f
        var frequency = moistureScale

        // Use 3 octaves for moisture variation
        repeat(moistureOctaves) {
            total += moistureNoise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        // Normalize to 0-1 range
        return (total + 1) / 2
    }

    /**
     * Creates a fallback terrain for when no matching terrain is found
     */
    private fun createFallbackTerrain(): Terrain {
        return Biome(
            BiomeData(
                "Fallback",
                terrains = listOf(
                    BiomeData.TerrainData("Fallback")
                ),
                color = MISSING_TILE_COLOR
            ),
            tileSize
        ).terrains.first()
    }

    /**
     * Data class holding noise values for a specific position
     */
    data class NoiseData(
        val x: Int,
        val y: Int,
        val elevation: Float,
        val temperature: Float,
        val moisture: Float
    )
}