package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.MineType

@SerialName("Gem")
enum class Gem(
    override val mineType: MineType = MineType.GEM
) : Harvestable {
    Amazonite,
    Carnelian,
    Diopside,
    Eudialyte,
    Hematite,
    Jadeite;

    override val locKey: String = name.lowercase()
}