package org.roldy.data.state

import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    val squads: MutableList<SquadState>,
    var lastSquad: Int = 0
)