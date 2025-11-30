package org.roldy.terrain

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
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
class ProceduralMapGenerator(
    private val seed: Long,
    private val width: Int,
    private val height: Int,
    private val tileSize: Int,
    private val elevationScale: Float = 0.0025f,
    private val moistureScale: Float = 0.005f,
    private val temperatureScale: Float = 0.1f,
    private val enableTransitions: Boolean = true,
    private val debugMode: Boolean = false
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
                logger.debug {
                    "Add transition layer to tile map"
                }
                tiledMap.layers.add(transitionLayer)
            }
        }
        logger.debug {
            "Add layer with biom colors"
        }
        tiledMap.layers.add(generateBiomeLayer())
        logger.debug {
            "Clearing terrain cache"
        }
        terrainCache.clear()
        return tiledMap
    }

    /**
     * Gets the terrain data as a 2D array for splatmap generation
     * This allows external systems (like SplatMapGenerator) to access terrain assignments
     */
    fun getTerrainData(): Array<Array<Terrain>> {
        // Ensure terrain cache is populated by generating the base layer if needed
        if (terrainCache.isEmpty()) {
            val biomes = loadBiomes(tileSize)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val noiseData = generateNoiseData(x, y)
                    val terrain = findTerrainForNoise(biomes, noiseData)
                    terrainCache[Pair(x, y)] = terrain
                }
            }
        }

        // Convert cache to 2D array
        return Array(width) { x ->
            Array(height) { y ->
                terrainCache[Pair(x, y)] ?: fallbackTerrain
            }
        }
    }

    /**
     * Gets debug information for a specific tile position
     * Returns a DebugInfo object containing position and terrain details
     * Useful for rendering debug overlays
     */
    fun getDebugInfo(x: Int, y: Int): DebugInfo? {
        if (!debugMode) return null
        if (x !in 0 until width || y !in 0 until height) return null

        val terrain = terrainCache[Pair(x, y)] ?: return null
        return DebugInfo(
            x = x,
            y = y,
            terrainName = terrain.data.name,
            biomeName = terrain.biome.data.name
        )
    }

    /**
     * Gets debug information for all tiles in the map
     * Returns a list of DebugInfo objects for rendering debug overlays
     */
    fun getAllDebugInfo(): List<DebugInfo> {
        if (!debugMode) return emptyList()

        return terrainCache.map { (pos, terrain) ->
            DebugInfo(
                x = pos.first,
                y = pos.second,
                terrainName = terrain.data.name,
                biomeName = terrain.biome.data.name
            )
        }
    }

    /**
     * Data class for debug information about a tile
     */
    data class DebugInfo(
        val x: Int,
        val y: Int,
        val terrainName: String,
        val biomeName: String
    )

    private fun generateBiomeLayer(): TiledMapTileLayer {
        val layer = TiledMapTileLayer(width, height, tileSize, tileSize)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val terrain = terrainCache.getValue(x to y)
                val cell = TiledMapTileLayer.Cell().apply {
                    tile = StaticTiledMapTile(terrain.biome.color)
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
                terrainCache[Pair(x, y)] = terrain

                // Create cell with the selected terrain
                val cell = TiledMapTileLayer.Cell().apply {
                    tile = StaticTiledMapTile(terrain.region)
                }

                // Add debug properties if debug mode is enabled
                if (debugMode) {
                    cell.tile.properties.put("debug_position", "($x, $y)")
                    cell.tile.properties.put("debug_terrain", terrain.data.name)
                    cell.tile.properties.put("debug_biome", terrain.biome.data.name)
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
        var transitionCount = 0

        for (x in 0 until width) {
            for (y in 0 until height) {
                val transitionTile = calculateTransitionTile(x, y)
                if (transitionTile != null) {
                    val cell = TiledMapTileLayer.Cell().apply {
                        tile = StaticTiledMapTile(transitionTile.region)
                    }

                    // Add debug properties if debug mode is enabled
                    if (debugMode) {
                        cell.tile.properties.put("debug_transition", "true")
                        cell.tile.properties.put("debug_position", "($x, $y)")
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
     * Calculates if a transition tile is needed at the given position
     * Returns a Terrain with edge texture region, or null if no transition is needed
     *
     * Uses RimWorld-style edge blending: transitions occur between any different terrains.
     * Priority determines which terrain's edge is shown (higher priority bleeds into lower).
     * For equal priority terrains, uses alphabetical name comparison as tiebreaker.
     * Supports both edge (cardinal) and corner (diagonal) transitions.
     */
    private fun calculateTransitionTile(x: Int, y: Int): Terrain? {
        val currentTerrain = terrainCache[Pair(x, y)] ?: return null
        val currentPriority = getTerrainPriority(currentTerrain.data.name)

        // Get neighbors in all 8 directions
        val northTerrain = if (y + 1 < height) terrainCache[Pair(x, y + 1)] else null
        val southTerrain = if (y - 1 >= 0) terrainCache[Pair(x, y - 1)] else null
        val eastTerrain = if (x + 1 < width) terrainCache[Pair(x + 1, y)] else null
        val westTerrain = if (x - 1 >= 0) terrainCache[Pair(x - 1, y)] else null
        val neTerrain = if (y + 1 < height && x + 1 < width) terrainCache[Pair(x + 1, y + 1)] else null
        val seTerrain = if (y - 1 >= 0 && x + 1 < width) terrainCache[Pair(x + 1, y - 1)] else null
        val swTerrain = if (y - 1 >= 0 && x - 1 >= 0) terrainCache[Pair(x - 1, y - 1)] else null
        val nwTerrain = if (y + 1 < height && x - 1 >= 0) terrainCache[Pair(x - 1, y + 1)] else null

        // First check cardinal directions for edge transitions
        // Edges take priority over corners
        val edgeRegion = when {
            northTerrain != null &&
                    northTerrain.data.name != currentTerrain.data.name &&
                    shouldShowTransition(currentTerrain.data.name, currentPriority, northTerrain.data.name) -> {
                // North neighbor should bleed into this tile
                val edgeName = "${northTerrain.data.name}_Edge_S"
                northTerrain.biome.atlas?.findRegion(edgeName)
            }

            southTerrain != null &&
                    southTerrain.data.name != currentTerrain.data.name &&
                    shouldShowTransition(currentTerrain.data.name, currentPriority, southTerrain.data.name) -> {
                // South neighbor should bleed into this tile
                val edgeName = "${southTerrain.data.name}_Edge_N"
                southTerrain.biome.atlas?.findRegion(edgeName)
            }

            eastTerrain != null &&
                    eastTerrain.data.name != currentTerrain.data.name &&
                    shouldShowTransition(currentTerrain.data.name, currentPriority, eastTerrain.data.name) -> {
                // East neighbor should bleed into this tile
                val edgeName = "${eastTerrain.data.name}_Edge_W"
                eastTerrain.biome.atlas?.findRegion(edgeName)
            }

            westTerrain != null &&
                    westTerrain.data.name != currentTerrain.data.name &&
                    shouldShowTransition(currentTerrain.data.name, currentPriority, westTerrain.data.name) -> {
                // West neighbor should bleed into this tile
                val edgeName = "${westTerrain.data.name}_Edge_E"
                westTerrain.biome.atlas?.findRegion(edgeName)
            }

            else -> null
        }

        // If we found an edge, return it (edges have priority)
        if (edgeRegion != null) {
            return Terrain(currentTerrain.biome, currentTerrain.color, currentTerrain.data, edgeRegion)
        }

        // Otherwise check diagonal corners (only when no edges are present)
        // This ensures corners only appear for isolated diagonal contacts
        data class CornerCandidate(val region: com.badlogic.gdx.graphics.g2d.TextureRegion, val priority: Int)

        val cornerCandidates = mutableListOf<CornerCandidate>()

        // NE corner: only show if N and E neighbors are same terrain
        if (neTerrain != null &&
            neTerrain.data.name != currentTerrain.data.name &&
            northTerrain?.data?.name == currentTerrain.data.name &&
            eastTerrain?.data?.name == currentTerrain.data.name &&
            shouldShowTransition(currentTerrain.data.name, currentPriority, neTerrain.data.name)
        ) {
            val cornerName = "${neTerrain.data.name}_Corner_Outer_SW"
            neTerrain.biome.atlas?.findRegion(cornerName)?.let {
                cornerCandidates.add(CornerCandidate(it, getTerrainPriority(neTerrain.data.name)))
            }
        }

        // SE corner: only show if S and E neighbors are same terrain
        if (seTerrain != null &&
            seTerrain.data.name != currentTerrain.data.name &&
            southTerrain?.data?.name == currentTerrain.data.name &&
            eastTerrain?.data?.name == currentTerrain.data.name &&
            shouldShowTransition(currentTerrain.data.name, currentPriority, seTerrain.data.name)
        ) {
            val cornerName = "${seTerrain.data.name}_Corner_Outer_NW"
            seTerrain.biome.atlas?.findRegion(cornerName)?.let {
                cornerCandidates.add(CornerCandidate(it, getTerrainPriority(seTerrain.data.name)))
            }
        }

        // SW corner: only show if S and W neighbors are same terrain
        if (swTerrain != null &&
            swTerrain.data.name != currentTerrain.data.name &&
            southTerrain?.data?.name == currentTerrain.data.name &&
            westTerrain?.data?.name == currentTerrain.data.name &&
            shouldShowTransition(currentTerrain.data.name, currentPriority, swTerrain.data.name)
        ) {
            val cornerName = "${swTerrain.data.name}_Corner_Outer_NE"
            swTerrain.biome.atlas?.findRegion(cornerName)?.let {
                cornerCandidates.add(CornerCandidate(it, getTerrainPriority(swTerrain.data.name)))
            }
        }

        // NW corner: only show if N and W neighbors are same terrain
        if (nwTerrain != null &&
            nwTerrain.data.name != currentTerrain.data.name &&
            northTerrain?.data?.name == currentTerrain.data.name &&
            westTerrain?.data?.name == currentTerrain.data.name &&
            shouldShowTransition(currentTerrain.data.name, currentPriority, nwTerrain.data.name)
        ) {
            val cornerName = "${nwTerrain.data.name}_Corner_Outer_SE"
            nwTerrain.biome.atlas?.findRegion(cornerName)?.let {
                cornerCandidates.add(CornerCandidate(it, getTerrainPriority(nwTerrain.data.name)))
            }
        }

        // Pick the corner with highest priority terrain
        val cornerRegion = cornerCandidates.maxByOrNull { it.priority }?.region

        return if (cornerRegion != null) {
            Terrain(currentTerrain.biome, currentTerrain.color, currentTerrain.data, cornerRegion)
        } else {
            null
        }
    }

    /**
     * Determines if a transition should be shown on the current tile based on neighbor
     * Returns true if neighbor has higher priority, or equal priority with alphabetically higher name
     */
    private fun shouldShowTransition(
        currentName: String,
        currentPriority: Int,
        neighborName: String
    ): Boolean {
        val neighborPriority = getTerrainPriority(neighborName)

        return when {
            neighborPriority > currentPriority -> true  // Higher priority always shows
            neighborPriority < currentPriority -> false // Lower priority never shows
            else -> neighborName > currentName          // Equal priority: use alphabetical tiebreaker
        }
    }

    /**
     * Gets the priority of a terrain type for transition rendering
     * Higher values = higher priority (will show transitions)
     * Lower values = lower priority (will not show transitions)
     */
    private fun getTerrainPriority(terrainName: String): Int {
        return when {
            // Water types have lowest priority (0-9)
            terrainName.startsWith("Water") -> 0

            // Beach/Sand types have medium priority (10-19)
            terrainName.startsWith("BeachSand") -> 10
            terrainName.startsWith("Sand") -> 10

            // Grass types have higher priority (20-29)
            terrainName.startsWith("Grass") -> 20

            // Stone/Rock types have highest priority (30+)
            terrainName.startsWith("Stone") -> 30
            terrainName.startsWith("Rock") -> 30

            // Default priority
            else -> 15
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