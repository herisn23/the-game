package org.roldy.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.system.exitProcess

private const val textureAssets = "Assets/Game Buffs/Free Stylized Textures/Textures"
private val sourceTexturesPath = Path("$sourceContext/$textureAssets")
private val texturesPath = Path("$spineContext/$textureAssets")
private val originalTargetTexturesPath = texturesPath.resolve("original")

private const val Albedo = "Albedo"


fun repackTerrain() {
    deleteDirectory(texturesPath.toFile())
    originalTargetTexturesPath.toFile().mkdirs()
    sourceTexturesPath.toFile().listFiles().forEach {
        if (it.isDirectory) {
            it.copyTerrainTextures()
        }
    }
//    createTransitions()
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

fun createTransitions() {
    EdgeTileGenerator.generateEdgeTiles(
        inputDir = originalTargetTexturesPath.absolutePathString(),
        outputDir = originalTargetTexturesPath.absolutePathString()
    )
}

val terrainSettings = TexturePacker.Settings().apply {
    // Atlas size - needs to fit multiple 2048x2048 textures
//    maxWidth = 4096
//    maxHeight = 4096

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
    premultiplyAlpha = true  // Set to false for PBR textures!
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
    grid = true  // Set to true if all textures are same size
    // If grid = true, it can be more efficient
}

private fun packTextures() {
    val repackTarget = texturesPath.resolve("repacked").apply {
        toFile().mkdir()
    }
    runCatching {
        TexturePacker.process(
            terrainSettings,
            originalTargetTexturesPath.absolutePathString(),
            repackTarget.absolutePathString(),
            "TileMap.atlas"
        )
        val atlas = TextureAtlas(Gdx.files.absolute("${repackTarget.absolutePathString()}/TileMap.atlas"))
        val names = atlas.regions.joinToString("\n") { it.name }
        val namesFile = repackTarget.resolve("TileNames").toFile()
        if (!namesFile.exists()) {
            namesFile.writeText(names)
        }
    }.onFailure {
        it.printStackTrace()
        exitProcess(0)
    }
}

private fun File.copyTerrainTextures() {
    val terrainType = name.normalize()
    listFiles().filter { it.extension == "png" }.forEach {
        if (it.nameWithoutExtension.contains(Albedo)) {
            copy(it.toPath(), originalTargetTexturesPath.resolve("$terrainType.png"))
        }
    }
}


private fun String.normalize() = replace(Regex("_\\d+$"), "")

object EdgeTileGenerator {

    fun generateEdgeTiles(inputDir: String, outputDir: String) {
        val inputFolder = File(inputDir)
        val outputFolder = File(outputDir)
        outputFolder.mkdirs()

        inputFolder.listFiles()?.filter {
            it.extension.lowercase() in listOf("png", "jpg", "jpeg")
        }?.forEach { file ->
            val image = ImageIO.read(file)
            val baseName = file.nameWithoutExtension

            // Copy original
            ImageIO.write(image, "png", File(outputFolder, "${baseName}.png"))

            // Single edges (4)
            generateEdge(image, "n", baseName, outputFolder)  // North
            generateEdge(image, "s", baseName, outputFolder)  // South
            generateEdge(image, "e", baseName, outputFolder)  // East
            generateEdge(image, "w", baseName, outputFolder)  // West

            // Opposite edges (2)
            generateEdge(image, "ns", baseName, outputFolder) // North-South
            generateEdge(image, "ew", baseName, outputFolder) // East-West

            // Convex corners (4)
            generateEdge(image, "ne", baseName, outputFolder) // Northeast
            generateEdge(image, "nw", baseName, outputFolder) // Northwest
            generateEdge(image, "se", baseName, outputFolder) // Southeast
            generateEdge(image, "sw", baseName, outputFolder) // Southwest

            // T-junctions (4)
            generateEdge(image, "nes", baseName, outputFolder) // North-East-South
            generateEdge(image, "nws", baseName, outputFolder) // North-West-South
            generateEdge(image, "new", baseName, outputFolder) // North-East-West
            generateEdge(image, "sew", baseName, outputFolder) // South-East-West

            // Cross (1)
            generateEdge(image, "nsew", baseName, outputFolder) // All directions

            println("Generated edge tiles for: $baseName")
        }
    }

    private fun generateEdge(
        source: BufferedImage,
        direction: String,
        baseName: String,
        outputFolder: File
    ) {
        val width = source.width
        val height = source.height
        val result = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = result.createGraphics()

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

        // Draw original image
        g2d.drawImage(source, 0, 0, null)

        // Create gradient mask
        g2d.composite = AlphaComposite.DstIn

        // Apply gradient(s) based on direction
        when (direction) {
            // Single edges
            "n" -> applyGradient(g2d, width, height, "n")
            "s" -> applyGradient(g2d, width, height, "s")
            "e" -> applyGradient(g2d, width, height, "e")
            "w" -> applyGradient(g2d, width, height, "w")

            // Opposite edges (combine two gradients)
            "ns" -> {
                applyGradient(g2d, width, height, "n")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "s")
            }
            "ew" -> {
                applyGradient(g2d, width, height, "e")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "w")
            }

            // Convex corners
            "ne", "nw", "se", "sw" -> applyCornerGradient(g2d, width, height, direction)

            // T-junctions (combine three gradients)
            "nes" -> {
                applyGradient(g2d, width, height, "n")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "e")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "s")
            }
            "nws" -> {
                applyGradient(g2d, width, height, "n")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "w")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "s")
            }
            "new" -> {
                applyGradient(g2d, width, height, "n")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "e")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "w")
            }
            "sew" -> {
                applyGradient(g2d, width, height, "s")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "e")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "w")
            }

            // Cross (combine all four)
            "nsew" -> {
                applyGradient(g2d, width, height, "n")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "s")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "e")
                g2d.composite = AlphaComposite.DstIn
                applyGradient(g2d, width, height, "w")
            }
        }

        g2d.dispose()

        // Save result
        val outputFile = File(outputFolder, "${baseName}_edge_${direction}.png")
        ImageIO.write(result, "png", outputFile)
    }

    private fun applyGradient(g2d: Graphics2D, width: Int, height: Int, direction: String) {
        val gradient = when (direction) {
            "n" -> GradientPaint(
                0f, height.toFloat(), java.awt.Color(255, 255, 255, 255),
                0f, 0f, java.awt.Color(255, 255, 255, 0)
            )
            "s" -> GradientPaint(
                0f, 0f, java.awt.Color(255, 255, 255, 255),
                0f, height.toFloat(), java.awt.Color(255, 255, 255, 0)
            )
            "e" -> GradientPaint(
                0f, 0f, java.awt.Color(255, 255, 255, 255),
                width.toFloat(), 0f, java.awt.Color(255, 255, 255, 0)
            )
            "w" -> GradientPaint(
                width.toFloat(), 0f, java.awt.Color(255, 255, 255, 255),
                0f, 0f, java.awt.Color(255, 255, 255, 0)
            )
            else -> return
        }

        g2d.paint = gradient
        g2d.fillRect(0, 0, width, height)
    }

    private fun applyCornerGradient(g2d: Graphics2D, width: Int, height: Int, corner: String) {
        // Use RadialGradientPaint for smoother corners
        val (centerX, centerY) = when (corner) {
            "ne" -> Pair(width.toFloat(), 0f)
            "nw" -> Pair(0f, 0f)
            "se" -> Pair(width.toFloat(), height.toFloat())
            "sw" -> Pair(0f, height.toFloat())
            else -> return
        }

        val radius = width.toFloat() * 1.414f // Diagonal distance

        val gradient = RadialGradientPaint(
            centerX, centerY,
            radius,
            floatArrayOf(0f, 1f),
            arrayOf(
                java.awt.Color(255, 255, 255, 255),
                java.awt.Color(255, 255, 255, 0)
            )
        )

        g2d.paint = gradient
        g2d.fillRect(0, 0, width, height)
    }
}