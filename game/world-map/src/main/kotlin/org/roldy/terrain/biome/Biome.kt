package org.roldy.terrain.biome

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.logger

val MISSING_TILE_COLOR: Color = Color.MAGENTA

data class Biome(
    val data: BiomeData,
    val tileSize: Int
) : AutoDisposableAdapter() {

    val atlas by disposable {
        val file = loadAsset("terrain/${data.name}.atlas")
        if (file.exists()) {
            logger.debug("Atlas found: ${data.name}")
            TextureAtlas(file)
        } else {
            logger.debug("Atlas not found: ${data.name}")
            null
        }
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
        biome.atlas?.findRegion(data.name) ?: default
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

internal fun AutoDisposable.loadBiomes(tileSize: Int, biomesConfiguration: String): List<Biome> =
    loadAsset(biomesConfiguration).readString()
        .run {
            Yaml.default.decodeFromString<BiomesConfiguration>(this)
        }
        .run {
            loadBiomes(this, tileSize)
        }

internal fun AutoDisposable.loadBiomes(configuration: BiomesConfiguration, tileSize: Int): List<Biome> =
    configuration.biomes.map {
        Biome(it, tileSize).apply {
            atlas?.let(disposables::add)
        }.disposable()
    }