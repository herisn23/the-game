package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Ore")
enum class Ore(
    override val type: HarvestableType = HarvestableType.ORE
) : Harvestable {
    Copper,
    Iron,
    Gold,
    Silver,
    Mithril,
    Adamantine;

    override val key: String = name.lowercase()
}