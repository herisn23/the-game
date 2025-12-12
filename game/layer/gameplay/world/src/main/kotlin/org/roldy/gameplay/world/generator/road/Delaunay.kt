package org.roldy.gameplay.world.generator.road

import org.roldy.gameplay.world.generator.hexDistance
import org.roldy.rendering.screen.world.populator.environment.SettlementData

object Delaunay: RoadNetworkAlgorithm {
    /**
     * config:
     * pruningFactor: Float // Remove 30% of longest edges
     */
    override fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>> {
        val pruningFactor:Float by config
        // Step 1: Create Delaunay triangulation (connects nearby settlements)
        val allEdges = mutableListOf<Pair<SettlementData, SettlementData>>()

        // Simple approach: connect each settlement to its Voronoi neighbors
        settlements.forEach { from ->
            settlements.filter { it != from }
                .sortedBy { hexDistance(from.coords, it.coords) }
                .take(6)  // Max 6 neighbors in hex
                .forEach { to ->
                    allEdges.add(from to to)
                }
        }

        // Step 2: Remove duplicates and sort by length
        val uniqueEdges = allEdges.distinctBy { setOf(it.first, it.second) }
            .sortedByDescending { hexDistance(it.first.coords, it.second.coords) }

        // Step 3: Remove longest edges (pruning)
        val keepCount = (uniqueEdges.size * (1 - pruningFactor)).toInt()
        return uniqueEdges.take(keepCount)
    }
}