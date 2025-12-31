package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Metal")
enum class Metal(
    override val type: HarvestableType = HarvestableType.METAL
) : Harvestable {
    Osmium,
    Titanium,
    Uranium,
    Zirconium,
    Chromium,
    Vanadium;

    override val locKey: String = name.lowercase()
}