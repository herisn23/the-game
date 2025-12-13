package org.roldy.data.tile.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.tile.mine.MineType

@SerialName("Gem")
enum class Gem(
    override val mineType: MineType = MineType.GEM
) : Harvestable {
    Amazonite,
    Carnelian,
    Diopside,
    Eudialyte,
    Hematite,
    Jadeite
}