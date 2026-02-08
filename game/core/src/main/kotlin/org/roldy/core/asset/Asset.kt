package org.roldy.core.asset

import com.badlogic.gdx.assets.AssetManager
import kotlin.reflect.KClass

interface Asset<T : Any> {
    val cls: KClass<T>
    fun get(): T
    fun load(assetManager: AssetManager)
}