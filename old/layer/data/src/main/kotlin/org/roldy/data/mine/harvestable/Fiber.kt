package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Fiber")
enum class Fiber(
    override val type: HarvestableType = HarvestableType.FIBER,
) : Harvestable {
    Flax,
    Cotton,
    Linen,
    Silk,
    SpiderSilk,
    MoonWeave;

    override val key: String = name.lowercase()
}