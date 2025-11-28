package org.roldy.equipment.atlas

import kotlinx.serialization.json.Json
import org.roldy.core.asset.loadAsset
import org.roldy.equipment.atlas.armor.ArmorAtlas

fun loadAtlasMetaData(relativeFilePath: String) =
    run {
        Json.decodeFromString<List<ArmorAtlas.Metadata>>(loadAsset("$relativeFilePath.meta").readString())
    }