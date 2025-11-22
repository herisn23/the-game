package org.roldy.equipment.atlas.armor

import kotlinx.serialization.Serializable
import org.roldy.asset.loadAtlasMetaData
import org.roldy.equipment.atlas.EquipmentAtlas

abstract class ArmorAtlas(atlasPath: String) : EquipmentAtlas("$atlasPath.atlas") {

    @Serializable
    data class Metadata(
        val name: String,
        val flags: Map<String, Boolean>
    )

    val meta by lazy {
        loadAtlasMetaData(atlasPath)
    }

    val map by lazy {
        meta.associateBy { it.name }
    }

    operator fun get(name: String): Metadata = map.getValue(name)
}