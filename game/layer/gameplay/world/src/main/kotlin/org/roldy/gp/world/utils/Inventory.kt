package org.roldy.gp.world.utils

import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.data.state.HeroState
import org.roldy.data.state.InventoryItemState
import org.roldy.data.state.InventoryState

object Inventory {


    private fun add(inventory: InventoryState, item: InventoryItemState): InventoryItemState {
        val indexes = inventory.items.map { it.index }.sorted()
        val nearestIndex = if (0 !in indexes) {
            0
        } else {
            indexes.asSequence()
                .zipWithNext()
                .firstOrNull { (current, next) -> next - current > 1 }
                ?.let { (current, _) -> current + 1 }
                ?: (indexes.last() + 1)
        }
        item.index = nearestIndex
        inventory.items.add(item)
        return item
    }

    fun add(inventory: InventoryState, harvestable: Harvestable, amount: Int): InventoryItemState {
        return inventory
            .items
            .find {
                it.harvestable == harvestable
            }?.also {
                it.amount += amount
            } ?: add(inventory, InventoryItemState(index = 0, harvestable = harvestable, amount = amount))
    }


    fun slots(heroState: HeroState) = 10f

}