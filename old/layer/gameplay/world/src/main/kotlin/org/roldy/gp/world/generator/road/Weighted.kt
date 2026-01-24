package org.roldy.gp.world.generator.road

import org.roldy.core.utils.hexDistance
import org.roldy.data.state.SettlementState


fun interface WeightedImportance {
    fun get(data: SettlementState): Int
}

object Weighted : RoadNetworkAlgorithm {

    /**
     * config:
     * importance: WeightedImportant
     */
    override fun generate(
        seed: Long,
        settlements: List<SettlementState>,
        config: Map<String, Any>
    ): List<Pair<SettlementState, SettlementState>> {
        val getImportance: WeightedImportance by config
        val edges = mutableListOf<Triple<SettlementState, SettlementState, Int>>()

        // Create weighted edges
        for (i in settlements.indices) {
            for (j in i + 1 until settlements.size) {
                val from = settlements[i]
                val to = settlements[j]
                val distance = hexDistance(from.coords, to.coords)
                val importance = getImportance.get(from) + getImportance.get(to)

                // Weight = distance / importance (lower is better)
                val weight = distance / importance.coerceAtLeast(1)
                edges.add(Triple(from, to, weight))
            }
        }

        // Use MST on weighted graph
        return buildMSTFromWeightedEdges(edges)
    }

    private fun buildMSTFromWeightedEdges(
        edges: List<Triple<SettlementState, SettlementState, Int>>
    ): List<Pair<SettlementState, SettlementState>> {
        val result = mutableListOf<Pair<SettlementState, SettlementState>>()
        val visited = mutableSetOf<SettlementState>()
        val sortedEdges = edges.sortedBy { it.third }

        // Kruskal's algorithm
        sortedEdges.forEach { (from, to, _) ->
            if (from !in visited || to !in visited) {
                result.add(from to to)
                visited.add(from)
                visited.add(to)
            }
        }

        return result
    }
}