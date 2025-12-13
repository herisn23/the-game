package org.roldy.data.state

import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    val pawn: PawnState
)