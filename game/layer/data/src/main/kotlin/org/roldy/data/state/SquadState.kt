package org.roldy.data.state

import kotlinx.serialization.Serializable

@Serializable
data class SquadState(
    val leader: HeroState,
    val heroes: List<HeroState>
)