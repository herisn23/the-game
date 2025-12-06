package org.roldy.terrain

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import org.roldy.core.Vector2Int
import org.roldy.core.logger
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
    val heightData: HeightData
) : HeightData by heightData

class ProceduralMapGenerator(
    val seed: Long,
    val width: Int,
    val height: Int,
    val tileSize: Int,
    val elevationScale: Float = 0.001f,    // Lower = smoother elevation changes
    val moistureScale: Float = 0.003f,      // Lower = larger moisture zones
    val temperatureScale: Float = 0.05f,    // Lower = larger temperature zones
    private val enableTransitions: Boolean = true
) {

    private val temperatureNoise = SimplexNoise(seed)
    private val moistureNoise = SimplexNoise(seed + 1)
    private val elevationNoise = SimplexNoise(seed + 2)

    // Cache for terrain at each position
    private val terrainCache = mutableMapOf<Vector2Int, TileData>()
    private val transitionResolver = TileTransitionResolver(width, height, terrainCache)
    private val fallbackTerrain = createFallbackTerrain()

    /**
     * Generates a complete TiledMap with biomes and optional transitions
     */
    fun generate(): TiledMap {
        val tiledMap = TiledMap()
        val biomes = loadBiomes(tileSize)
        // Generate base terrain layer
        tiledMap.layers.add(generateBaseLayer(biomes))

        // Add transition layers if enabled
        if (enableTransitions) {
            tiledMap.layers.add(generateTransitionLayer())
        }
        return tiledMap
    }

    val terrainData: Map<Vector2Int, TileData> get() = terrainCache

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
     * Generates multiple transition layers for smooth terrain blending
     * Creates separate layers for each transition type and direction
     * This allows tiles to show all necessary transitions simultaneously
     */
    /**
     * Generates a transition layer for smooth terrain blending
     * This layer sits on top of the base layer and contains transition tiles
     */
    private fun generateTransitionLayer(): TiledMapTileLayer? {
        val layer = TiledMapTileLayer(width, height, tileSize, tileSize)
        var transitionCount = 0

        for (x in 0 until width) {
            for (y in 0 until height) {
                val transitionTile = transitionResolver.calculateTransitionTile(x, y)
                if (transitionTile != null) {
                    val cell = TiledMapTileLayer.Cell().apply {
                        tile = StaticTiledMapTile(transitionTile.region)

                    }

                    layer.setCell(x, y, cell)
                    transitionCount++
                }
            }
        }

        logger.info { "Generated transition layer with $transitionCount transition tiles" }
        return if (transitionCount > 0) layer else null
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
            noiseData.elevation <= biome.data.elevation &&
                    noiseData.temperature <= biome.data.temperature &&
                    noiseData.moisture <= biome.data.moisture
        }

        // Find matching terrain within biome
        val terrain = biome?.terrains?.find { terrain ->
            noiseData.elevation <= terrain.data.elevation &&
                    noiseData.temperature <= terrain.data.temperature &&
                    noiseData.moisture <= terrain.data.moisture
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
        repeat(4) {
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
        return latitudeFactor * 0.85f + noise * temperatureScale
    }

    /**
     * Generates moisture noise with multiple octaves
     */
    private fun getMoisture(x: Int, y: Int): Float {
        var total = 0.0f
        var amplitude = 1f
        var frequency = moistureScale

        // Use 3 octaves for moisture variation
        repeat(3) {
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
        override val elevation: Float,
        override val temperature: Float,
        override val moisture: Float
    ) : HeightData
}