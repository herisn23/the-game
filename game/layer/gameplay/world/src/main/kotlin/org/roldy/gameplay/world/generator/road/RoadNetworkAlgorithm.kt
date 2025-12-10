package org.roldy.gameplay.world.generator.road

import org.roldy.rendering.scene.world.populator.environment.SettlementData

interface RoadNetworkAlgorithm {

    fun generate(
        seed: Long,
        settlements: List<SettlementData>,
        config: Map<String, Any>
    ): List<Pair<SettlementData, SettlementData>>
}