package org.roldy.gameplay.scene

import org.roldy.core.Vector2Int
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.map.WorldMap

class TerrainFinder(
    val map: WorldMap
) {

    // Option 1: Find forest tile with most forest neighbors (deepest in forest)
    fun findDeepestTerrain(type: BiomeType): Vector2Int {
        var bestForest: Vector2Int? = null
        var maxForestNeighbors = 0

        map.terrainData.forEach { (coords, data) ->
            if (data.terrain.biome.data.type == type) {
                // Count how many forest tiles surround this one
                val forestNeighbors = countNeighbors(coords, radius = 3, type)

                if (forestNeighbors > maxForestNeighbors) {
                    maxForestNeighbors = forestNeighbors
                    bestForest = coords
                }
            }
        }

        return bestForest ?: Vector2Int(0, 0)
    }

    // Option 2: Find forest tile with minimum distance to forest edges (centermost)
    fun findCenterMostTerrain(type: BiomeType): Vector2Int {
        val forestTiles = map.terrainData.filter { (_, data) ->
            data.terrain.biome.data.type == type
        }.keys.toList()

        if (forestTiles.isEmpty()) return Vector2Int(0, 0)

        // For each forest tile, find minimum distance to a non-forest tile
        var bestForest: Vector2Int? = null
        var maxDistanceToEdge = 0f

        forestTiles.forEach { coords ->
            val distanceToEdge = findDistanceToEdge(coords, type)

            if (distanceToEdge > maxDistanceToEdge) {
                maxDistanceToEdge = distanceToEdge
                bestForest = coords
            }
        }

        return bestForest ?: forestTiles.first()
    }

    // Option 3: Simpler - just avoid forest tiles adjacent to non-forest
    fun findTerrain(type: BiomeType): Vector2Int {
        val deepForestTiles = map.terrainData.filter { (coords, data) ->
            if (data.terrain.biome.data.type != type) return@filter false

            // Check immediate neighbors - all must be forest
            val neighbors = listOf(
                Vector2Int(coords.x - 1, coords.y),
                Vector2Int(coords.x + 1, coords.y),
                Vector2Int(coords.x, coords.y - 1),
                Vector2Int(coords.x, coords.y + 1)
            )

            neighbors.all { neighbor ->
                map.terrainData[neighbor]?.terrain?.biome?.data?.type == type
            }
        }

        return deepForestTiles.keys.randomOrNull() ?: Vector2Int(0, 0)
    }

    // Helper: Count forest tiles within a radius
    private fun countNeighbors(center: Vector2Int, radius: Int, type: BiomeType): Int {
        var count = 0
        for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                if (dx == 0 && dy == 0) continue // Skip center

                val neighbor = Vector2Int(center.x + dx, center.y + dy)
                val terrainData = map.terrainData[neighbor]

                if (terrainData?.terrain?.biome?.data?.type == type) {
                    count++
                }
            }
        }
        return count
    }

    // Helper: Find distance to nearest non-forest tile
    private fun findDistanceToEdge(center: Vector2Int, type: BiomeType): Float {
        var minDistance = Float.MAX_VALUE

        // Check in expanding rings until we find a non-forest tile
        for (radius in 1..10) {
            for (dx in -radius..radius) {
                for (dy in -radius..radius) {
                    // Only check tiles at current radius (ring, not filled circle)
                    if (kotlin.math.abs(dx) != radius && kotlin.math.abs(dy) != radius) continue

                    val neighbor = Vector2Int(center.x + dx, center.y + dy)
                    val terrainData = map.terrainData[neighbor]

                    // If this is NOT forest, calculate distance
                    if (terrainData?.terrain?.biome?.data?.type != type) {
                        val distance = center.distanceTo(neighbor)
                        minDistance = kotlin.math.min(minDistance, distance)

                        // Early exit - we found edge at this radius
                        if (minDistance <= radius.toFloat()) {
                            return minDistance
                        }
                    }
                }
            }

            // If we found an edge at this radius, return
            if (minDistance < Float.MAX_VALUE) {
                return minDistance
            }
        }

        return minDistance
    }

    // Helper extension function
    fun Vector2Int.distanceTo(other: Vector2Int): Float {
        val dx = (x - other.x).toFloat()
        val dy = (y - other.y).toFloat()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

}