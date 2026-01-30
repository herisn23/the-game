package org.roldy.core.map

import org.roldy.core.IVector2Int
import org.roldy.core.Vector2Int
import kotlin.math.abs
import kotlin.math.sqrt

data class FlatArea(
    val center: Vector2Int,
    val averageSlope: Float,
    val elevation: Float
)

fun Map<IVector2Int, NoiseData>.findFlatAreas(
    count: Int = 5,
    areaSize: Int = 10,
    minDistance: Int = 50,  // Minimum distance between areas
    elevationRanger: ClosedRange<Float> = 0.1f..0.2f,
): List<FlatArea> {
    val noiseData = this
    val results = mutableListOf<FlatArea>()
    val width = noiseData.keys.maxOf { it.x }
    val height = noiseData.keys.maxOf { it.y }
    val halfSize = areaSize / 2

    // Calculate all areas with their scores
    val candidates = mutableListOf<FlatArea>()

    val step = maxOf(1, areaSize / 2)

    for (centerX in halfSize until (width - halfSize) step step) {
        for (centerY in halfSize until (height - halfSize) step step) {
            val centerPos = Vector2Int(centerX, centerY)
            val centerData = noiseData[centerPos] ?: continue

            if (centerData.elevation !in elevationRanger) continue

            var totalSlope = 0f
            var sampleCount = 0

            for (dx in -halfSize..halfSize) {
                for (dy in -halfSize..halfSize) {
                    val pos = Vector2Int(centerX + dx, centerY + dy)
                    val data = noiseData[pos] ?: continue

                    val e = data.elevation
                    val left = noiseData[Vector2Int(pos.x - 1, pos.y)]?.elevation ?: e
                    val right = noiseData[Vector2Int(pos.x + 1, pos.y)]?.elevation ?: e
                    val up = noiseData[Vector2Int(pos.x, pos.y - 1)]?.elevation ?: e
                    val down = noiseData[Vector2Int(pos.x, pos.y + 1)]?.elevation ?: e

                    totalSlope += maxOf(
                        abs(e - left),
                        abs(e - right),
                        abs(e - up),
                        abs(e - down)
                    )
                    sampleCount++
                }
            }

            if (sampleCount > 0) {
                candidates.add(FlatArea(centerPos, totalSlope / sampleCount, centerData.elevation))
            }
        }
    }

    // Sort by slope and pick best non-overlapping areas
    candidates.sortBy { it.averageSlope }

    for (candidate in candidates) {
        if (results.size >= count) break

        // Check distance from existing results
        val tooClose = results.any { existing ->
            val dx = candidate.center.x - existing.center.x
            val dy = candidate.center.y - existing.center.y
            sqrt((dx * dx + dy * dy).toFloat()) < minDistance
        }

        if (!tooClose) {
            results.add(candidate)
        }
    }

    return results
}