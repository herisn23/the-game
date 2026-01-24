package org.roldy.gp.world.generator.road

import org.roldy.data.state.SettlementState


interface RoadNetworkAlgorithm {

    fun generate(
        seed: Long,
        settlements: List<SettlementState>,
        config: Map<String, Any>
    ): List<Pair<SettlementState, SettlementState>>
}