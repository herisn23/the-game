package org.roldy.gameplay.scene.initializers

import org.roldy.core.Vector2Int
import org.roldy.data.state.GameState
import org.roldy.data.state.MineState
import org.roldy.data.state.PawnState
import org.roldy.data.state.PlayerState
import org.roldy.data.state.RulerState
import org.roldy.data.state.SettlementState
import org.roldy.gameplay.world.generator.data.MineData
import org.roldy.gameplay.world.generator.data.SettlementData


fun createGameState(
    settlements: List<SettlementData>,
    mines: List<MineData>,
    findSuitableSpotForPlayer: () -> Vector2Int
): GameState = GameState(
    settlements = settlements.map {
        it.toState(mines)
    },
    mines = mines.filter { it.settlementData == null }.map(MineData::toState),
    player = PlayerState(
        pawn = PawnState(
            coords = findSuitableSpotForPlayer()
        )
    )
)

private fun SettlementData.toState(mines: List<MineData>) =
    SettlementState(
        coords = coords,
        ruler = RulerState(color = color),
        mines = mines.filter { it.settlementData == this }.map(MineData::toState),
        region = radiusCoords,
        texture = texture,
    )

private fun MineData.toState() = MineState(
    coords = coords,
    harvestable = harvestable,
    max = 10,
    current = 10
)
