package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.MineType

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