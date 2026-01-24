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

    fun add(
        hero: HeroState, harvestable: Harvestable, amount: Int,
        inventoryIsFull: () -> Unit = {},
        added: (InventoryItemState) -> Unit = { _ -> }
    ) {
        if (amount == 0) return
        val item = hero.inventory
            .items
            .find {
                it.harvestable == harvestable
            }?.also {
                it.amount += amount
            } ?: run {
            if (hasSpace(hero)) {
                add(hero.inventory, InventoryItemState(index = 0, harvestable = harvestable, amount = amount))
            } else {
                null
            }
        }

        if (item == null) {
            inventoryIsFull()
        } else {
            added(item)
        }
    }


    /**
     * TODO Calculate real inventory size by default size, abilities, mounts, states, etc.
     */
    fun size(heroState: HeroState) = heroState.inventory.defaultSize

    fun hasSpace(hero: HeroState) = size(hero) > hero.inventory.items.size

}