package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Wood")
enum class Wood(
    override val type: HarvestableType = HarvestableType.WOOD
): Harvestable {
    Pine,
    Oak,
    Ash,
    Yew,
    Ironwood,
    Elderwood;

    override val key: String = name.lowercase()
}