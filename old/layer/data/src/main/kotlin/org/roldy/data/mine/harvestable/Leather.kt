package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Leather")
enum class Leather(
    override val type: HarvestableType = HarvestableType.LEATHER
) : Harvestable {
    Rawhide,
    BeastHide,
    ThickHide,
    ScaledLeather,
    OldLeather,
    FurLeather;

    override val key: String = name.lowercase()
}