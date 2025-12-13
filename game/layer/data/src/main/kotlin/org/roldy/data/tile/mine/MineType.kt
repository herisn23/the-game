package org.roldy.data.tile.mine

import org.roldy.data.tile.mine.harvestable.Fiber
import org.roldy.data.tile.mine.harvestable.Gem
import org.roldy.data.tile.mine.harvestable.Harvestable
import org.roldy.data.tile.mine.harvestable.Metal
import org.roldy.data.tile.mine.harvestable.Wood

enum class MineType(
    val texture: String,
    val harvestable: () -> List<Harvestable>
) {
    METAL("hexHillsMine00", { Metal.entries }),
    WOOD("hexForestBroadleafForester00", { Wood.entries }),
    FIBER("hexPlainsFarm00", { Fiber.entries }),
    GEM("hexDirtClayPit01", { Gem.entries });

    companion object {
        val harvestable get() = MineType.entries.flatMap { it.harvestable() }
    }
}