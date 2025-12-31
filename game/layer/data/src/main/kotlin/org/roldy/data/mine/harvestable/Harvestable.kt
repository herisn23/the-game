package org.roldy.data.mine.harvestable

import kotlinx.serialization.Serializable
import org.roldy.data.mine.HarvestableType

@Serializable
sealed interface Harvestable {
    val type: HarvestableType
    val name: String
    val locKey: String
}