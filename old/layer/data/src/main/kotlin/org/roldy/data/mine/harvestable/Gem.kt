package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Gem")
enum class Gem(
    override val type: HarvestableType = HarvestableType.GEM
) : Harvestable {
    RoughQuartz,
    Amber,
    Emerald,
    Sapphire,
    Ruby,
    Starstone;

    override val key: String = name.lowercase()
}