package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)


object AtlasLoader {
    val settlements get() = load("environment/Settlements.atlas")
    val roads get() = load("environment/Roads.atlas")
    val mountains get() = load("environment/mountains/Cold.atlas")

    fun load(name: String) =
        TextureAtlas(loadAsset(name))
}
