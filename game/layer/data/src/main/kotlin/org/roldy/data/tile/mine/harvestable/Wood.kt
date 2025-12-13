package org.roldy.data.tile.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.tile.mine.MineType

@SerialName("Wood")
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