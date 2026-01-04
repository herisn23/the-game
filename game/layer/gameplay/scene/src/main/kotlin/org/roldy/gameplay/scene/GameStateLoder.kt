package org.roldy.gameplay.scene

import org.roldy.data.map.MapData
import org.roldy.data.state.*
import org.roldy.data.tile.HarvestableTileData
import org.roldy.data.tile.SettlementTileData


fun createGameState(
    mapData: MapData,
    settlements: List<SettlementTileData>,
    mines: List<HarvestableTileData>,
    hero: HeroState
): GameState = GameState(
    mapData = mapData,
    settlements = settlements.map(SettlementTileData::toState),
    mines = mines.map(HarvestableTileData::toState),
    player = PlayerState(
        mutableListOf(
            SquadState(
                hero,
                emptyList()
            )
        )
    )
)

private fun SettlementTileData.toState() =
    SettlementState(
        id = id,
        coords = coords,
        ruler = RulerState(color = color),
        region = claims
    )

private fun HarvestableTileData.toState() = HarvestableState(
    coords = coords,
    harvestable = harvestable,
    settlement = settlementData?.id,
    refreshing = RefreshingState()
)
