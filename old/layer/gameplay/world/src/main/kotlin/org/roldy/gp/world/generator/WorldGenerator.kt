package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int

interface WorldGenerator<D> {
    val occupied: (Vector2Int) -> Boolean
    fun generate(): List<D>
}