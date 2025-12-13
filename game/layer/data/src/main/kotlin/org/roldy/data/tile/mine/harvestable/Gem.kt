package org.roldy.data.tile.mine.harvestable

import org.roldy.data.tile.mine.MineType

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