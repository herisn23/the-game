package org.roldy.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

interface AssetDestination {
    val path: String
}

object BodyDestination : AssetDestination {
    override val path: String = "sprites/body"
}

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)

fun loadAsset(fileName: String, destination: AssetDestination): FileHandle =
    loadAsset("${destination.path}/$fileName")