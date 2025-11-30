package org.roldy.terrain

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
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
 */
class ProceduralMapGenerator(
    private val seed: Long,
    private val width: Int,
    private val height: Int,
    private val tileSize: Int,
    private val elevationScale: Float = 0.0025f,
    private val moistureScale: Float = 0.005f,
    private val temperatureScale: Float = 0.1f,
    private val enableTransitions: Boolean = true
) {

    private val temperatureNoise = SimplexNoise(seed)
    private val moistureNoise = SimplexNoise(seed + 1)
    private val elevationNoise = SimplexNoise(seed + 2)

    // Cache for terrain at each position
    private val terrainCache = mutableMapOf<Pair<Int, Int>, Terrain>()

    // Transition resolver for auto-tiling
    private val transitionResolver = TileTransitionResolver(use8WayTransitions = true)

    private val fallbackTerrain = createFallbackTerrain()

    /**
     * Generates a complete TiledMap with biomes and optional transitions
     */
    fun generate(): TiledMap {
        val tiledMap = TiledMap()
        val biomes = loadBiomes(tileSize)

        // Generate base terrain layer
        val baseLayer = generateBaseLayer(biomes)
        tiledMap.layers.add(baseLayer)

        // Add transition layer if enabled
        if (enableTransitions) {
            val transitionLayer = generateTransitionLayer()
            if (transitionLayer != null) {
                tiledMap.layers.add(transitionLayer)
            }
        }

        return tiledMap
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
                terrainCache[Pair(x, y)] = terrain

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
     * Generates a transition layer for smooth terrain blending
     * This layer sits on top of the base layer and contains transition tiles
     */
    private fun generateTransitionLayer(): TiledMapTileLayer? {
        val layer = TiledMapTileLayer(width, height, tileSize, tileSize)
        var hasAnyTransition = false

        for (x in 0 until width) {
            for (y in 0 until height) {
                val transitionTile = calculateTransitionTile(x, y)
                if (transitionTile != null) {
                    val cell = TiledMapTileLayer.Cell().apply {
                        tile = StaticTiledMapTile(transitionTile.region)
                    }
                    layer.setCell(x, y, cell)
                    hasAnyTransition = true
                }
            }
        }

        return if (hasAnyTransition) layer else null
    }

    /**
     * Calculates if a transition tile is needed at the given position
     * Returns a TextureRegion for the transition tile, or null if no transition is needed
     */
    private fun calculateTransitionTile(x: Int, y: Int): Terrain? {
        val currentTerrain = terrainCache[Pair(x, y)] ?: return null

        // Get all 8 neighbors
        val neighbors = getNeighborTerrains(x, y)

        // Calculate transition using the resolver
        val transitionInfo = transitionResolver.calculateTransition(currentTerrain, neighbors)
            ?: return null // No transition needed

        // Try to find the transition tile in the atlas
        val transitionRegion = transitionResolver.findTransitionTile(transitionInfo)

        // If we found a transition tile, create a Terrain for it
        return if (transitionRegion != null) {
            // Create a temporary terrain with the transition tile
            // This allows the transition to use the existing Terrain infrastructure
            Terrain(
                currentTerrain.biome,
                currentTerrain.color,
                currentTerrain.data
            ).apply {
                region = transitionRegion
            }
        } else {
            null // No transition tile found in atlas
        }
    }

    /**
     * Gets the terrain of all 8 neighboring tiles
     * Order: N, E, S, W, NE, SE, SW, NW
     * Returns exactly 8 elements (nulls for out-of-bounds neighbors)
     */
    private fun getNeighborTerrains(x: Int, y: Int): List<Terrain?> {
        val directions = listOf(
            Pair(0, 1),   // North
            Pair(1, 0),   // East
            Pair(0, -1),  // South
            Pair(-1, 0),  // West
            Pair(1, 1),   // NE
            Pair(1, -1),  // SE
            Pair(-1, -1), // SW
            Pair(-1, 1)   // NW
        )

        return directions.map { (dx, dy) ->
            val nx = x + dx
            val ny = y + dy
            if (nx in 0 until width && ny in 0 until height) {
                terrainCache[Pair(nx, ny)]
            } else {
                null
            }
        }
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

        return terrain ?: fallbackTerrain
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
            BiomesSettings(
                mapOf("default" to com.badlogic.gdx.graphics.Color.MAGENTA)
            ),
            BiomeData(
                "Fallback",
                terrains = listOf(
                    BiomeData.TerrainData("Fallback", "default")
                )
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