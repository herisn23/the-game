package org.roldy.unity

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.system.exitProcess

private val textureAssets = mapOf(
    "forest" to "Assets/Texture_Me/Grass Forest Floor - 20 Textures (Bundle #1)/Textures"
)
private val sourceTexturesPath = textureAssets.map {
    it.key to Path("$sourceContext/${it.value}")
}.toMap()

private val texturesPath = textureAssets.map {
    it.key to Path("$spineContext/${it.value}")
}.toMap()
private val originalTargetTexturesPath = texturesPath.map {
    it.key to it.value.resolve("original")
}.toMap()

private const val targetSize = 400f
private val scaleDown = targetSize / 2048f

fun repackTerrain() {
    texturesPath.forEach {
        deleteDirectory(it.value.toFile())
    }
    originalTargetTexturesPath.forEach {
        it.value.toFile().mkdirs()
    }
    sourceTexturesPath.forEach {
        val target = originalTargetTexturesPath.getValue(it.key)
        it.value.toFile().listFiles().forEachIndexed { index, file ->
            if(file.extension == "png") {
                copy(file.toPath(), target.resolve("${it.key.capitalize()}${file.nameWithoutExtension.getNumber()}.png"))
            }
        }
    }

    packTextures()
    copyPackedTextures()
}

fun copyPackedTextures() {
    val assetsTarget = Path("assets/terrain")
    deleteDirectory(assetsTarget.toFile())
    assetsTarget.toFile().mkdirs()
    texturesPath.forEach {
        it.value.resolve("repacked").toFile().listFiles().forEach {
            copy(it.toPath(), assetsTarget.resolve(it.name))
        }
    }
}

val terrainSettings = TexturePacker.Settings().apply {
    // Atlas size - needs to fit multiple 2048x2048 textures
    maxWidth = 4096
    maxHeight = 4096

    // If you have many textures, allow multiple pages
    // Each page will be maxWidth x maxHeight
    pot = true  // Power of Two (recommended for older GPUs)

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
    scale = floatArrayOf(scaleDown)  // 200 x 200

    // Debug
    debug = false  // Set to true to see atlas bounds
    combineSubdirectories = false
    flattenPaths = false

    // Grid settings (if packing uniformly sized textures)
    grid = true  // Set to true if all textures are same size
    // If grid = true, it can be more efficient
}

private fun packTextures() {

    texturesPath.forEach {
        val repackTarget = it.value.resolve("repacked").apply {
            toFile().mkdir()
        }
        runCatching {
            TexturePacker.process(
                terrainSettings,
                originalTargetTexturesPath.getValue(it.key).absolutePathString(),
                repackTarget.absolutePathString(),
                "${it.key.capitalize()}.atlas"
            )
//            val atlas = TextureAtlas(Gdx.files.absolute("${repackTarget.absolutePathString()}/${it.key}.atlas"))
//            val names = atlas.regions.joinToString("\n") { it.name }
//            val namesFile = repackTarget.resolve("TileNames").toFile()
//            if (!namesFile.exists()) {
//                namesFile.writeText(names)
//            }
        }.onFailure {
            it.printStackTrace()
            exitProcess(0)
        }
    }
}

fun String.getNumber() = """\d+""".toRegex().find(this)?.value?.toInt() ?: 0