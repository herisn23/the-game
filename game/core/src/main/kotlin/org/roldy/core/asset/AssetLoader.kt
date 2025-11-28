package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)

