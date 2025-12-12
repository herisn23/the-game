package org.roldy.data

import kotlinx.serialization.Serializable
import org.roldy.data.pawn.PawnData

@Serializable
data class GameState(
    val pawn: PawnData,
)