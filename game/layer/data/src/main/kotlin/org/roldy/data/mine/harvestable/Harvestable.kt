package org.roldy.data.mine.harvestable

import kotlinx.serialization.Serializable
import org.roldy.data.mine.MineType

@Serializable
sealed interface Harvestable {
    val mineType: MineType
    val name: String
}