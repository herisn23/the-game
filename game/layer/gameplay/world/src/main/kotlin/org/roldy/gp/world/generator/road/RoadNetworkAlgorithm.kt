package org.roldy.gp.world.generator.road

import org.roldy.data.tile.SettlementTileData


interface RoadNetworkAlgorithm {

    fun generate(
        seed: Long,
        settlements: List<SettlementTileData>,
        config: Map<String, Any>
    ): List<Pair<SettlementTileData, SettlementTileData>>
}