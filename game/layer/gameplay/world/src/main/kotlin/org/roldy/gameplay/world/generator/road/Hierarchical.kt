package org.roldy.gameplay.world.generator.road

import org.roldy.gameplay.world.generator.hexDistance
import org.roldy.rendering.scene.world.populator.environment.SettlementData

object Hierarchical : RoadNetworkAlgorithm {
    /**
     * config:
     * tierLevels: Int
     * randomness: Float = 0.3f  // 0 = pure MST, 1 = very random
     * seed: Int // seed for randomness to keep randomness factor same for seeded map
     */
    override fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>> {
        val tierLevels: Int by config
        val allEdges = mutableListOf<Pair<SettlementData, SettlementData>>()

        // Tier 1: Major highways (MST of distant settlements)
        val tier1Settlements = settlements
            .chunked(settlements.size / tierLevels)
            .map { it.first() }
        allEdges.addAll(MinimumSpanningTree.generate(seed, tier1Settlements, config))

        // Tier 2: Regional roads (connect to tier 1)
        settlements.filterNot { it in tier1Settlements }.forEach { settlement ->
            val nearest = tier1Settlements.minByOrNull {
                hexDistance(settlement.coords, it.coords)
            }
            nearest?.let { allEdges.add(settlement to it) }
        }

        return allEdges
    }
}