package org.roldy.data.mine

import org.roldy.data.mine.harvestable.*

enum class HarvestableType(
    val texture: String,
    val harvestable: () -> List<Harvestable>
) {
    METAL("hexHillsMine00", { Metal.entries }),
    WOOD("hexForestBroadleafForester00", { Wood.entries }),
    FIBER("hexPlainsFarm00", { Fiber.entries }),
    GEM("hexDirtClayPit01", { Gem.entries });

    companion object {
        val harvestable get() = entries.flatMap { it.harvestable() }
    }
    val locKey = name.lowercase()
}