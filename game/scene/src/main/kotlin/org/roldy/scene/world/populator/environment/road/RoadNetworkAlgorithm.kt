package org.roldy.scene.world.populator.environment.road

import org.roldy.scene.world.populator.environment.SettlementData

interface RoadNetworkAlgorithm {

    fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>>
}