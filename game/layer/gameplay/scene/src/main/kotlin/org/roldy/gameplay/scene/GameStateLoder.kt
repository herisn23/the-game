package org.roldy.gameplay.scene

import org.roldy.core.Vector2Int
import org.roldy.data.map.MapData
import org.roldy.data.state.*
import org.roldy.data.tile.MineTileData
import org.roldy.data.tile.SettlementTileData


fun createGameState(
    mapData: MapData,
    settlements: List<SettlementTileData>,
    mines: List<MineTileData>,
    findSuitableSpotForPlayer: () -> Vector2Int
): GameState = GameState(
    mapData = mapData,
    settlements = settlements.map(SettlementTileData::toState),
    mines = mines.map(MineTileData::toState),
    player = PlayerState(
        pawn = PawnState(
            coords = findSuitableSpotForPlayer()
        )
    )
)

private fun SettlementTileData.toState() =
    SettlementState(
        id = id,
        coords = coords,
        ruler = RulerState(color = color),
        region = claims,
        texture = texture,
    )

private fun MineTileData.toState() = MineState(
    coords = coords,
    harvestable = harvestable,
    settlement = settlementData?.id,
    refreshing = RefreshingState()
)
