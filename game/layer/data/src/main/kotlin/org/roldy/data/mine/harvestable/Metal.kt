package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.MineType

@SerialName("Metal")
enum class Metal(
    override val mineType: MineType = MineType.METAL
) : Harvestable {
    Osmium,
    Titanium,
    Uranium,
    Zirconium,
    Chromium,
    Vanadium;

    override val locKey: String = name.lowercase()
}