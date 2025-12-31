package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.data.item.ItemGrade

@Serializable
data class InventoryState(
    val items: MutableList<InventoryItemState> = mutableListOf(),
)

@Serializable
data class InventoryItemState(
    var index: Int,
    var grade: ItemGrade?,
    var amount: Int,
    var lock: Boolean
)