package org.roldy.rendering.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.logger
import org.roldy.data.configuration.biome.BiomeData
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable

data class Biome(
    val data: BiomeData,
    val tileSize: Int
) : AutoDisposableAdapter() {

    val atlas by disposable {
        AtlasLoader.load("terrain/${data.name}.atlas")
    }
    val terrains by lazy {
        data.terrains.map {
            Terrain(this, data.color, it).disposable()
        }
    }
    val color by lazy {
        Pixmap(tileSize, tileSize, Pixmap.Format.RGBA8888).run {
            setColor(data.color)
            fill()
            TextureRegion(Texture(this).disposable())
        }
    }

}

class Terrain(
    val biome: Biome,
    val color: Color,
    val data: BiomeData.TerrainData
) : AutoDisposableAdapter() {

    val region: TextureRegion by lazy {
        biome.atlas.findRegion(data.name) ?: default
    }

    private val default by lazy {
        Pixmap(biome.tileSize, biome.tileSize, Pixmap.Format.RGBA8888).run {
            logger.debug("Region ${data.name} not found in Atlas ${biome.data.name}")
            setColor(color)
            fill()
            TextureRegion(Texture(this).disposable())
        }
    }

}