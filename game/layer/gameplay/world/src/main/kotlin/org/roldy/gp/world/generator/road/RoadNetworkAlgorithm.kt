package org.roldy.gp.world.generator.road

import org.roldy.gp.world.generator.data.SettlementData


interface RoadNetworkAlgorithm {

    fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>>
}