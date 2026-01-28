package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.Texture
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.utils.get
import org.roldy.g3d.Biome

class BiomeTextureManager : AutoDisposableAdapter() {
    val atlas by disposable { AtlasLoader.terrain }

    private val textures by lazy {
        Biome.entries.associateWith { biome -> atlas[biome.textureName].texture.disposable() }
    }

    init {
        println("Loaded ${textures.size} biome textures")
    }

    operator fun get(biome: Biome): Texture = textures.getValue(biome)

    override fun dispose() {
        textures.values.forEach { it.dispose() }
    }
}