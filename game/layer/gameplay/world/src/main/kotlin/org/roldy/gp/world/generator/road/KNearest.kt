package org.roldy.gp.world.generator.road

import org.roldy.core.utils.hexDistance
import org.roldy.data.tile.SettlementTileData

object KNearest : RoadNetworkAlgorithm {

    /**
     * Builds K-Nearest Neighbors network.
     * Creates road network where each settlement connects to K nearest neighbors.
     * config:
     * k: Int
     */
    override fun generate(
        seed: Long,
        settlements: List<SettlementTileData>,
        config: Map<String, Any>
    ): List<Pair<SettlementTileData, SettlementTileData>> {
        val k: Int by config
        val edges = mutableListOf<Pair<SettlementTileData, SettlementTileData>>()
        val processedPairs = mutableSetOf<Set<SettlementTileData>>()

        settlements.forEach { from ->
            val nearest = settlements
                .filter { it != from }
                .sortedBy { to -> hexDistance(from.coords, to.coords) }
                .take(k)

            nearest.forEach { to ->
                val pair = setOf(from, to)
                if (pair !in processedPairs) {
                    processedPairs.add(pair)
                    edges.add(from to to)
                }
            }
        }

        return edges
    }
}