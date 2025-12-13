package org.roldy.data.tile.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.tile.mine.MineType

enum class Metal(
    override val mineType: MineType = MineType.METAL
) : Harvestable {
    Osmium,
    Titanium,
    Uranium,
    Zirconium,
    Chromium,
    Vanadium
}