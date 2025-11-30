package org.roldy.terrain

import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.terrain.biome.Terrain

/**
 * Resolves tile transitions using bitmask-based auto-tiling.
 *
 * This system creates smooth transitions between different terrain types by:
 * - Analyzing neighboring tiles
 * - Calculating a bitmask based on terrain similarity
 * - Looking up appropriate transition tiles from the atlas
 *
 * Bitmask Layout (for 8 neighbors):
 * ```
 *   1   2   4
 *   8  [X] 16
 *  32  64 128
 * ```
 *
 * For 4-neighbor (cardinal only) transitions:
 * ```
 *       1
 *   2  [X]  4
 *       8
 * ```
 */
class TileTransitionResolver(
    private val use8WayTransitions: Boolean = true
) {

    /**
     * Calculates the appropriate transition for a tile based on its neighbors
     *
     * @param currentTerrain The terrain at the current position
     * @param neighbors List of neighboring terrains in order: N, E, S, W, NE, SE, SW, NW
     * @return TransitionInfo containing the bitmask and suggested tile name, or null if no transition needed
     */
    fun calculateTransition(
        currentTerrain: Terrain,
        neighbors: List<Terrain?>
    ): TransitionInfo? {
        require(neighbors.size == 8) { "Must provide exactly 8 neighbors" }

        // Calculate bitmask based on matching neighbors
        val bitmask = calculateBitmask(currentTerrain, neighbors)

        // If all neighbors match (bitmask == 255 or all relevant bits set), no transition needed
        if (isFullyMatchingBitmask(bitmask)) {
            return null
        }

        // Generate transition info
        val transitionType = determineTransitionType(bitmask)
        val tileName = generateTransitionTileName(currentTerrain, transitionType, bitmask)

        return TransitionInfo(
            bitmask = bitmask,
            transitionType = transitionType,
            suggestedTileName = tileName,
            baseTerrain = currentTerrain
        )
    }

    /**
     * Attempts to find a transition tile region from the terrain's atlas
     *
     * @param transitionInfo The transition information
     * @return TextureRegion for the transition tile, or null if not found
     */
    fun findTransitionTile(transitionInfo: TransitionInfo): TextureRegion? {
        val atlas = transitionInfo.baseTerrain.biome.atlas ?: return null

        // Try different naming conventions for transition tiles
        val possibleNames = listOf(
            // Format: TerrainName_Transition_Bitmask (e.g., "Grass_T_47")
            "${transitionInfo.baseTerrain.data.name}_T_${transitionInfo.bitmask}",

            // Format: TerrainName_TransitionType (e.g., "Grass_Edge_North")
            "${transitionInfo.baseTerrain.data.name}_${transitionInfo.transitionType.name}",

            // Format: TerrainName_Auto_Bitmask (e.g., "Grass_Auto_47")
            "${transitionInfo.baseTerrain.data.name}_Auto_${transitionInfo.bitmask}",

            // Direct suggestion
            transitionInfo.suggestedTileName
        )

        for (name in possibleNames) {
            val region = atlas.findRegion(name)
            if (region != null) {
                return region
            }
        }

        return null
    }

    /**
     * Calculates a bitmask based on which neighbors match the current terrain
     */
    private fun calculateBitmask(currentTerrain: Terrain, neighbors: List<Terrain?>): Int {
        var mask = 0

        // Cardinal directions and diagonals
        val positions = if (use8WayTransitions) {
            listOf(0, 1, 2, 3, 4, 5, 6, 7) // All 8 neighbors
        } else {
            listOf(0, 1, 2, 3) // Only cardinal directions
        }

        positions.forEach { index ->
            val neighbor = neighbors.getOrNull(index)
            if (neighbor != null && isSameTerrain(currentTerrain, neighbor)) {
                // Set the corresponding bit
                mask = mask or getBitForPosition(index)
            }
        }

        return mask
    }

    /**
     * Determines if two terrains should be considered the same for transition purposes
     */
    private fun isSameTerrain(terrain1: Terrain, terrain2: Terrain): Boolean {
        // Can be customized based on your needs
        // For now, exact match on terrain name
        return terrain1.data.name == terrain2.data.name
    }

    /**
     * Gets the bit value for a neighbor position
     * Positions: N=0, E=1, S=2, W=3, NE=4, SE=5, SW=6, NW=7
     */
    private fun getBitForPosition(position: Int): Int {
        return when (position) {
            0 -> 1    // North
            1 -> 2    // East
            2 -> 4    // South
            3 -> 8    // West
            4 -> 16   // NE
            5 -> 32   // SE
            6 -> 64   // SW
            7 -> 128  // NW
            else -> 0
        }
    }

    /**
     * Checks if the bitmask represents a fully matching tile (no transitions needed)
     */
    private fun isFullyMatchingBitmask(bitmask: Int): Boolean {
        return if (use8WayTransitions) {
            bitmask == 255 // All 8 bits set
        } else {
            (bitmask and 15) == 15 // All 4 cardinal bits set
        }
    }

    /**
     * Determines the type of transition based on the bitmask
     */
    private fun determineTransitionType(bitmask: Int): TransitionType {
        // Check for corners (2 adjacent cardinals missing)
        if ((bitmask and 3) == 0) return TransitionType.CORNER_OUTER_NE
        if ((bitmask and 6) == 0) return TransitionType.CORNER_OUTER_SE
        if ((bitmask and 12) == 0) return TransitionType.CORNER_OUTER_SW
        if ((bitmask and 9) == 0) return TransitionType.CORNER_OUTER_NW

        // Check for edges (1 cardinal missing)
        if ((bitmask and 1) == 0) return TransitionType.EDGE_NORTH
        if ((bitmask and 2) == 0) return TransitionType.EDGE_EAST
        if ((bitmask and 4) == 0) return TransitionType.EDGE_SOUTH
        if ((bitmask and 8) == 0) return TransitionType.EDGE_WEST

        // Check for inner corners (diagonal missing but cardinals present)
        if ((bitmask and 16) == 0 && (bitmask and 3) == 3) return TransitionType.CORNER_INNER_NE
        if ((bitmask and 32) == 0 && (bitmask and 6) == 6) return TransitionType.CORNER_INNER_SE
        if ((bitmask and 64) == 0 && (bitmask and 12) == 12) return TransitionType.CORNER_INNER_SW
        if ((bitmask and 128) == 0 && (bitmask and 9) == 9) return TransitionType.CORNER_INNER_NW

        return TransitionType.COMPLEX
    }

    /**
     * Generates a suggested tile name for the transition
     */
    private fun generateTransitionTileName(
        terrain: Terrain,
        type: TransitionType,
        bitmask: Int
    ): String {
        return "${terrain.data.name}_${type.tileSuffix}_$bitmask"
    }

    /**
     * Information about a tile transition
     */
    data class TransitionInfo(
        val bitmask: Int,
        val transitionType: TransitionType,
        val suggestedTileName: String,
        val baseTerrain: Terrain
    )

    /**
     * Types of transitions that can occur
     */
    enum class TransitionType(val tileSuffix: String) {
        EDGE_NORTH("Edge_N"),
        EDGE_EAST("Edge_E"),
        EDGE_SOUTH("Edge_S"),
        EDGE_WEST("Edge_W"),
        CORNER_OUTER_NE("Corner_Outer_NE"),
        CORNER_OUTER_SE("Corner_Outer_SE"),
        CORNER_OUTER_SW("Corner_Outer_SW"),
        CORNER_OUTER_NW("Corner_Outer_NW"),
        CORNER_INNER_NE("Corner_Inner_NE"),
        CORNER_INNER_SE("Corner_Inner_SE"),
        CORNER_INNER_SW("Corner_Inner_SW"),
        CORNER_INNER_NW("Corner_Inner_NW"),
        COMPLEX("Complex")
    }
}