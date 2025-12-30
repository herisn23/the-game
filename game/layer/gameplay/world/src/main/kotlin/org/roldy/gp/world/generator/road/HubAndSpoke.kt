package org.roldy.gp.world.generator.road

import org.roldy.core.utils.hexDistance
import org.roldy.data.tile.SettlementTileData

object HubAndSpoke: RoadNetworkAlgorithm {
    /**
     * config:
     * hubCount: Int
     */
    override fun generate(
        seed: Long,
        settlements: List<SettlementTileData>,
        config: Map<String, Any>
    ): List<Pair<SettlementTileData, SettlementTileData>> {
       val hubCount: Int by config
        val edges = mutableListOf<Pair<SettlementTileData, SettlementTileData>>()

        // Identify major hubs (largest/most central settlements)
        val hubs = settlements
            .sortedByDescending { settlement ->
                // Score by centrality (or use population if you have it)
                settlements.sumOf { hexDistance(settlement.coords, it.coords).toDouble() }
            }
            .take(hubCount)

        // Connect hubs to each other (backbone)
        hubs.forEach { hub1 ->
            hubs.filter { it != hub1 }
                .minByOrNull { hub2 -> hexDistance(hub1.coords, hub2.coords) }
                ?.let { nearestHub ->
                    edges.add(hub1 to nearestHub)
                }
        }

        // Connect each non-hub settlement to nearest hub
        settlements.filterNot { it in hubs }.forEach { settlement ->
            val nearestHub = hubs.minByOrNull { hub ->
                hexDistance(settlement.coords, hub.coords)
            }
            nearestHub?.let { edges.add(settlement to it) }
        }

        return edges
    }
}