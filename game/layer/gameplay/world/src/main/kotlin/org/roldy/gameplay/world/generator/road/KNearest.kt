package org.roldy.gameplay.world.generator.road

import org.roldy.core.utils.hexDistance
import org.roldy.gameplay.world.generator.data.SettlementData

object KNearest : RoadNetworkAlgorithm {

    /**
     * Builds K-Nearest Neighbors network.
     * Creates road network where each settlement connects to K nearest neighbors.
     * config:
     * k: Int
     */
    override fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>> {
        val k: Int by config
        val edges = mutableListOf<Pair<SettlementData, SettlementData>>()
        val processedPairs = mutableSetOf<Set<SettlementData>>()

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