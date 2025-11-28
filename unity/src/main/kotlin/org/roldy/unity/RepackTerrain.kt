package org.roldy.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.system.exitProcess

private const val textureAssets = "Assets/Game Buffs/Free Stylized Textures/Textures"
private val sourceTexturesPath = Path("$sourceContext/$textureAssets")
private val texturesPath = Path("$spineContext/$textureAssets")
private val originalTargetTexturesPath = texturesPath.resolve("original")

private const val Normal = "Normal"
private const val Metallic = "Metallic"
private const val Height = "Height"
private const val AO = "AO"
private const val Roughness = "Roughness"
private const val Mask = "Mask"
private const val Albedo = "Albedo"
private val types = listOf(
    Albedo
)


fun repackTerrain() {
    deleteDirectory(texturesPath.toFile())
    types.forEach {
        originalTargetTexturesPath.resolve(it).toFile().mkdirs()
    }
    sourceTexturesPath.toFile().listFiles().forEach {
        if (it.isDirectory) {
            it.copyTerrainTextures()
        }
    }
    packTextures()
    copyPackedTextures()
}
fun copyPackedTextures() {
    val assetsTarget = Path("assets/terrain")
    deleteDirectory(assetsTarget.toFile())
    assetsTarget.toFile().mkdirs()
    texturesPath.resolve("repacked").toFile().listFiles().forEach {
        copy(it.toPath(), assetsTarget.resolve(it.name))
    }

}

val terrainSettings = TexturePacker.Settings().apply {
    // Atlas size - needs to fit multiple 2048x2048 textures
//    maxWidth = 4096
//    maxHeight = 4096

    // If you have many textures, allow multiple pages
    // Each page will be maxWidth x maxHeight
//    pot = true  // Power of Two (recommended for older GPUs)

    // Padding to prevent texture bleeding
    paddingX = 2 // Increased for large textures
    paddingY = 2
    duplicatePadding = true  // Duplicate edge pixels into padding
    edgePadding = true  // Add padding at atlas edges

    // Filtering
    filterMin = TextureFilter.Linear  // Best quality with mipmaps
    filterMag = TextureFilter.Linear

    // Format
    format = Pixmap.Format.RGBA8888  // Full quality

    // Alpha handling
    premultiplyAlpha = false  // Set to false for PBR textures!
    // PBR textures (normal maps, roughness, etc.) should NOT have premultiplied alpha

    // Packing algorithm
    stripWhitespaceX = false  // Keep for terrain tiles
    stripWhitespaceY = false

    // Don't rotate textures (important for normal maps!)
    rotation = false

    // Aliases and duplicates
    alias = true  // Remove duplicate images

    // Output
    outputFormat = "png"
    scale = floatArrayOf(.09765625f)  // 200 x 200

    // Debug
    debug = false  // Set to true to see atlas bounds
    combineSubdirectories = false
    flattenPaths = false

    // Grid settings (if packing uniformly sized textures)
    grid = false  // Set to true if all textures are same size
    // If grid = true, it can be more efficient
}
private fun packTextures() {
    val repackTarget = texturesPath.resolve("repacked").apply {
        toFile().mkdir()
    }
    originalTargetTexturesPath.toFile().listFiles().forEach {
        val textureType = it.name
        runCatching {
            TexturePacker.process(
                terrainSettings,
                it.toPath().absolutePathString(),
                repackTarget.absolutePathString(),
                "TileMap.atlas"
            )
            val atlas = TextureAtlas(Gdx.files.absolute("${repackTarget.absolutePathString()}/TileMap.atlas"))
            val names = atlas.regions.joinToString("\n") { it.name }
            val namesFile = repackTarget.resolve("TileNames").toFile()
            if(!namesFile.exists()) {
                namesFile.writeText(names)
            }
        }.onFailure {
            it.printStackTrace()
            exitProcess(0)
        }
    }
}

private fun File.copyTerrainTextures() {
    val terrainType = name.normalize()
    listFiles().filter { it.extension == "png" }.forEach {
        it.nameWithoutExtension.type?.apply {
            copy(it.toPath(), originalTargetTexturesPath.resolve(this).resolve("$terrainType.png"))
        }
    }
}

private val String.type
    get() = types.find {
        contains(it)
    }

private fun String.normalize() = replace(Regex("_\\d+$"), "")