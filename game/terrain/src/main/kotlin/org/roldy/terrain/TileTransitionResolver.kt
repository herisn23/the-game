package org.roldy.terrain

import org.roldy.terrain.biome.Terrain

class TileTransitionResolver(
    val width: Int,
    val height: Int,
    val terrainCache: Map<Pair<Int, Int>, Terrain>
) {
    /**
     * Data class to hold all transition types for a tile
     */
    fun calculateTransitionTile(x: Int, y: Int): Terrain? {
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
    private fun getTerrainPriority(biomeName: String): Int {
        return when {
            // Water types have lowest priority (0-9)
            biomeName.startsWith("Water") -> 0

            // Stone/Rock types have highest priority (30+)
            biomeName.startsWith("Snow") -> 10
            biomeName.startsWith("Ice") -> 10

            biomeName.startsWith("Desert") -> 20
            biomeName.startsWith("DeepDesert") -> 20
            biomeName.startsWith("Savanna") -> 20


            // Grass types have higher priority (20-29)
            biomeName.startsWith("Forest") -> 30
            biomeName.startsWith("Jungle") -> 30
            biomeName.startsWith("Swamp") -> 30

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

}