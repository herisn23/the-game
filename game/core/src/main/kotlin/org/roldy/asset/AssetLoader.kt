package org.roldy.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.json.Json
import org.roldy.equipment.atlas.armor.ArmorAtlas

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)

fun loadAtlasMetaData(relativeFilePath:String) =
    run {
        Json.decodeFromString<List<ArmorAtlas.Metadata>>(loadAsset("$relativeFilePath.meta").readString())
    }