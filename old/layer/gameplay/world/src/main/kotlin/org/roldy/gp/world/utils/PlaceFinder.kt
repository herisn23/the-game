package org.roldy.gp.world.utils

import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.async
import org.roldy.data.state.Positioned

object PlaceFinder {

    fun <P : Positioned> find(
        list: List<P>, coords: Vector2Int,
        onFound: (P) -> Unit
    ) {
        async { main ->
            val found = list.find { mine -> mine.coords == coords }
            main {
                found?.let {
                    onFound(found)
                }
            }
        }
    }
}