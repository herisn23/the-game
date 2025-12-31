package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Gem")
enum class Gem(
    override val type: HarvestableType = HarvestableType.GEM
) : Harvestable {
    Amazonite,
    Carnelian,
    Diopside,
    Eudialyte,
    Hematite,
    Jadeite;

    override val locKey: String = name.lowercase()
}