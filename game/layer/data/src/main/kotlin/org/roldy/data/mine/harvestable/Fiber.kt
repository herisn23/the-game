package org.roldy.data.mine.harvestable

import kotlinx.serialization.SerialName
import org.roldy.data.mine.MineType

@SerialName("Fiber")
enum class Fiber(
    override val mineType: MineType = MineType.FIBER,
) : Harvestable {
    Flax,
    Hemp,
    Kenaf,
    Jute,
    Coir,
    Cotton
}