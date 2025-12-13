package org.roldy.data.tile.mine.harvestable

import kotlinx.serialization.Serializable
import org.roldy.data.tile.mine.MineType

@Serializable
sealed interface Harvestable {
    val mineType: MineType
}