package org.roldy.gameplay.world.generator.road

import org.roldy.core.times
import org.roldy.gameplay.world.generator.hexDistance
import org.roldy.rendering.screen.world.populator.environment.SettlementData
import kotlin.random.Random


object MinimumSpanningTree : RoadNetworkAlgorithm {
    /**
     * Builds Minimum Spanning Tree using Prim's algorithm.
     * Creates minimal road network connecting all settlements.
     * config:
     * randomness: Float = 0.3f  // 0 = pure MST, 1 = very random
     */
    override fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>> {
        val randomness: Float by config
        val edges = mutableListOf<Pair<SettlementData, SettlementData>>()
        val visited = mutableSetOf<SettlementData>()


        visited.add(settlements.random(Random(seed)))

        while (visited.size < settlements.size) {
            val candidates = mutableListOf<Triple<SettlementData, SettlementData, Float>>()

            visited.forEach { from ->
                settlements.filter { it !in visited }.forEach { to ->
                    val distance = hexDistance(from.coords, to.coords)
                    val multiplied = from.coords * to.coords
                    val random = Random(seed + multiplied.sum)
                    // Add randomness to distance
                    val randomizedDistance = distance * (1 + random.nextFloat() * randomness)
                    candidates.add(Triple(from, to, randomizedDistance))
                }
            }

            val chosen = candidates.minByOrNull { it.third }
            chosen?.let { (from, to, _) ->
                edges.add(from to to)
                visited.add(to)
            }
        }

        return edges
    }
}