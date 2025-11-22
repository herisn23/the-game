package org.roldy.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import kotlinx.serialization.json.Json
import org.roldy.pawn.skeleton.PawnArmorSlotData

interface AssetDestination {
    val path: String
}

object BodyDestination : AssetDestination {
    override val path: String = "sprites/body"
}

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)

fun loadAtlasWithMeta(relativeFilePath:String) =
    run {
        val atlas = TextureAtlas(loadAsset("$relativeFilePath.atlas"))
        val meta = Json.decodeFromString<List<PawnArmorSlotData.Metadata>>(loadAsset("$relativeFilePath.meta").readString())
        atlas to meta
    }