package org.roldy.gameplay.scene.initializers

import org.roldy.core.Vector2Int
import org.roldy.data.map.MapData
import org.roldy.data.state.GameState
import org.roldy.data.state.MineState
import org.roldy.data.state.PawnState
import org.roldy.data.state.PlayerState
import org.roldy.data.state.RefreshingState
import org.roldy.data.state.RulerState
import org.roldy.data.state.SettlementState
import org.roldy.gp.world.generator.data.MineData
import org.roldy.gp.world.generator.data.SettlementData


fun createGameState(
    mapData: MapData,
    settlements: List<SettlementData>,
    mines: List<MineData>,
    findSuitableSpotForPlayer: () -> Vector2Int
): GameState = GameState(
    mapData = mapData,
    settlements = settlements.map(SettlementData::toState),
    mines = mines.map(MineData::toState),
    player = PlayerState(
        pawn = PawnState(
            coords = findSuitableSpotForPlayer()
        )
    )
)

private fun SettlementData.toState() =
    SettlementState(
        id = id,
        coords = coords,
        ruler = RulerState(color = color),
        region = radiusCoords,
        texture = texture,
    )

private fun MineData.toState() = MineState(
    coords = coords,
    harvestable = harvestable,
    settlement = settlementData?.id,
    refreshing = RefreshingState()
)
