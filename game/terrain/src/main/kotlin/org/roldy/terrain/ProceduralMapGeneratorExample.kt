package org.roldy.terrain

/**
 * Example usage of the ProceduralMapGenerator
 *
 * This file demonstrates how to use the procedural map generator with biomes and tile transitions.
 */
object ProceduralMapGeneratorExample {

    /**
     * Basic usage: Generate a map with default settings
     */
    fun basicExample() {
        val generator = ProceduralMapGenerator(
            seed = 12345L,
            width = 100,
            height = 100,
            tileSize = 32
        )

        val tiledMap = generator.generate()
        // Use the tiledMap with your rendering system
    }

    /**
     * Advanced usage: Customize noise parameters for different terrain styles
     */
    fun advancedExample() {
        // Create a map with larger, smoother terrain features
        val smoothGenerator = ProceduralMapGenerator(
            seed = 67890L,
            width = 200,
            height = 200,
            tileSize = 32,
            elevationScale = 0.001f,    // Lower = smoother elevation changes
            moistureScale = 0.003f,      // Lower = larger moisture zones
            temperatureScale = 0.05f,    // Lower = larger temperature zones
            enableTransitions = true
        )

        val smoothMap = smoothGenerator.generate()

        // Create a map with more detailed, varied terrain
        val detailedGenerator = ProceduralMapGenerator(
            seed = 11111L,
            width = 200,
            height = 200,
            tileSize = 32,
            elevationScale = 0.005f,     // Higher = more varied elevation
            moistureScale = 0.01f,       // Higher = more varied moisture
            temperatureScale = 0.2f,     // Higher = more varied temperature
            enableTransitions = true
        )

        val detailedMap = detailedGenerator.generate()
    }

    /**
     * Disable transitions if your atlas doesn't have transition tiles
     */
    fun withoutTransitions() {
        val generator = ProceduralMapGenerator(
            seed = 99999L,
            width = 150,
            height = 150,
            tileSize = 32,
            enableTransitions = false  // Disable transitions
        )

        val tiledMap = generator.generate()
    }

    /**
     * Generate multiple maps with the same seed for consistency
     */
    fun consistentGeneration() {
        val seed = 42L

        val map1 = ProceduralMapGenerator(
            seed = seed,
            width = 100,
            height = 100,
            tileSize = 32
        ).generate()

        val map2 = ProceduralMapGenerator(
            seed = seed,  // Same seed produces identical map
            width = 100,
            height = 100,
            tileSize = 32
        ).generate()

        // map1 and map2 will be identical
    }

    /**
     * Generate different variations by changing the seed
     */
    fun generateVariations() {
        val seeds = listOf(100L, 200L, 300L, 400L, 500L)

        val maps = seeds.map { seed ->
            ProceduralMapGenerator(
                seed = seed,
                width = 100,
                height = 100,
                tileSize = 32
            ).generate()
        }

        // Each map will have different terrain but similar characteristics
    }
}

/**
 * Tile Transition Atlas Naming Conventions
 *
 * For transitions to work properly, your texture atlas should include transition tiles
 * following these naming conventions:
 *
 * 1. Bitmask-based naming: `{TerrainName}_T_{Bitmask}`
 *    Example: "Grass_T_47"
 *
 * 2. Type-based naming: `{TerrainName}_{TransitionType}`
 *    Examples:
 *    - "Grass_Edge_N" (north edge)
 *    - "Grass_Edge_E" (east edge)
 *    - "Grass_Corner_Outer_NE" (outer northeast corner)
 *    - "Grass_Corner_Inner_SE" (inner southeast corner)
 *
 * 3. Auto-tile naming: `{TerrainName}_Auto_{Bitmask}`
 *    Example: "Grass_Auto_47"
 *
 * Bitmask values for 8-way transitions:
 * ```
 *   1   2   4
 *   8  [X] 16
 *  32  64 128
 * ```
 *
 * Common transition patterns:
 * - Full tile (no transitions): 255
 * - Edge North: 254 (all except north)
 * - Edge South: 251 (all except south)
 * - Edge East: 253 (all except east)
 * - Edge West: 247 (all except west)
 * - Corner Outer NE: 252 (missing north and east)
 * - Corner Inner NE: 239 (present but diagonal missing)
 *
 * If no transition tiles are found in the atlas, the base terrain tile will be used instead.
 */