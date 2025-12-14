package org.roldy.gameplay.world.generator.road

import org.roldy.gameplay.world.generator.data.SettlementData

interface RoadNetworkAlgorithm {

    fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>>
}