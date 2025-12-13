package org.roldy.data.tile.mine.harvestable

import org.roldy.data.tile.mine.MineType

enum class Fiber(
    override val mineType: MineType = MineType.FIBER,
) : Harvestable {
    Flax,
    Hemp,
    Kenaf,
    Jute,
    Coir,
    Cotton
}