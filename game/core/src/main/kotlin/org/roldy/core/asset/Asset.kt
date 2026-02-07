package org.roldy.core.asset

import com.badlogic.gdx.assets.AssetManager

interface Asset<T> {
    fun get(): T
    fun load(assetManager: AssetManager)
}