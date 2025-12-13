package org.roldy.data.tile.mine.harvestable

import org.roldy.data.tile.mine.MineType

enum class Wood(
    override val mineType: MineType = MineType.WOOD
): Harvestable {
    Alder,
    Aspen,
    Ebony,
    Mahogany,
    Maple,
    Oak
}