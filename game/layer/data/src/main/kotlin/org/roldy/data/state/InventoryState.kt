package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.data.item.ItemGrade
import org.roldy.data.mine.harvestable.Harvestable

@Serializable
data class InventoryState(
    val items: MutableList<InventoryItemState> = mutableListOf(),
)

@Serializable
data class InventoryItemState(
    var index: Int,
    var harvestable: Harvestable? = null,
    var grade: ItemGrade? = null,
    var amount: Int = 0,
    var lock: Boolean = false
)