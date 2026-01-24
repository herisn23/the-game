package org.roldy.gp.world.utils

import org.roldy.core.Vector2Int
import org.roldy.data.state.SquadState

object Squad {


    fun calculateSpeed(squad: SquadState) = 1f


    fun updatePosition(position: Vector2Int, squad: SquadState) {
        squad.leader.coords = position
        squad.heroes.forEach {
            it.coords = position
        }
    }
}