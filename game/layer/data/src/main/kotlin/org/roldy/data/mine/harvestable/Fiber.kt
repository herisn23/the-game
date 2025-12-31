package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.HarvestableType

@SerialName("Fiber")
enum class Fiber(
    override val type: HarvestableType = HarvestableType.FIBER,
) : Harvestable {
    Flax,
    Hemp,
    Kenaf,
    Jute,
    Coir,
    Cotton;

    override val locKey: String = name.lowercase()
}