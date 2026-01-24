package org.roldy.data.mine

import org.roldy.data.mine.harvestable.*

enum class HarvestableType(
    val texture: String,
    val harvestable: () -> List<Harvestable>
) {
    ORE("hexHillsMine00", { Ore.entries }),
    WOOD("hexForestBroadleafForester00", { Wood.entries }),
    FIBER("hexPlainsFarm00", { Fiber.entries }),
    GEM("hexDirtClayPit01", { Gem.entries }),
    LEATHER("hexDirtClayPit01", { Leather.entries });

    companion object {
        val harvestable get() = entries.flatMap { it.harvestable() }
    }
    val key = name.lowercase()
}