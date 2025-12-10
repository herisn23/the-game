package org.roldy.rendering.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import java.nio.file.Path
import kotlin.io.path.absolutePathString

/**
 * Generates tile transitions for smooth terrain blending.
 *
 * Creates edge and corner transition tiles by blending the base terrain
 * with neighboring terrain patterns.
 */
object TransitionTileGenerator {

    /**
     * Transition tile type with naming suffix and blend pattern
     */
    enum class TransitionType(
        val suffix: String,
        val description: String
    ) {
        // Edge transitions (4 cardinal directions)
        EDGE_NORTH("Edge_N", "North edge transition"),
        EDGE_EAST("Edge_E", "East edge transition"),
        EDGE_SOUTH("Edge_S", "South edge transition"),
        EDGE_WEST("Edge_W", "West edge transition"),

        // Outer corner transitions (4 corners)
        CORNER_OUTER_NE("Corner_Outer_NE", "Northeast outer corner"),
        CORNER_OUTER_SE("Corner_Outer_SE", "Southeast outer corner"),
        CORNER_OUTER_SW("Corner_Outer_SW", "Southwest outer corner"),
        CORNER_OUTER_NW("Corner_Outer_NW", "Northwest outer corner"),

        // Inner corner transitions (4 corners)
        CORNER_INNER_NE("Corner_Inner_NE", "Northeast inner corner"),
        CORNER_INNER_SE("Corner_Inner_SE", "Southeast inner corner"),
        CORNER_INNER_SW("Corner_Inner_SW", "Southwest inner corner"),
        CORNER_INNER_NW("Corner_Inner_NW", "Northwest inner corner");

        companion object {
            fun essential() = listOf(
                EDGE_NORTH, EDGE_EAST, EDGE_SOUTH, EDGE_WEST,
                CORNER_OUTER_NE, CORNER_OUTER_SE, CORNER_OUTER_SW, CORNER_OUTER_NW
            )

            fun all() = values().toList()
        }
    }

    /**
     * Generates all transition tiles for a given terrain texture
     *
     * @param sourceTexturePath Path to the base terrain texture
     * @param outputDir Directory where transition tiles will be saved
     * @param textureName Base name for the terrain (e.g., "Grass", "Sand")
     * @param generateInnerCorners If true, generates all 12 transitions; if false, only 8 essential ones
     */
    fun generateTransitions(
        sourceTexturePath: Path,
        outputDir: Path,
        textureName: String,
        generateInnerCorners: Boolean = false
    ) {
        val sourcePixmap = Pixmap(Gdx.files.absolute(sourceTexturePath.absolutePathString()))
        val width = sourcePixmap.width
        val height = sourcePixmap.height

        val transitions = if (generateInnerCorners) {
            TransitionType.all()
        } else {
            TransitionType.essential()
        }

        println("Generating ${transitions.size} transition tiles for $textureName...")

        transitions.forEach { type ->
            val transitionPixmap = generateTransitionTile(sourcePixmap, type, width, height)
            val outputPath = outputDir.resolve("${textureName}_${type.suffix}.png")

            PixmapIO.writePNG(Gdx.files.absolute(outputPath.absolutePathString()), transitionPixmap)
            transitionPixmap.dispose()

            println("  Generated: ${textureName}_${type.suffix}.png")
        }

        sourcePixmap.dispose()
        println("Finished generating transitions for $textureName")
    }

    /**
     * Generates a single transition tile based on the transition type
     */
    private fun generateTransitionTile(
        source: Pixmap,
        type: TransitionType,
        width: Int,
        height: Int
    ): Pixmap {
        val transition = Pixmap(width, height, Pixmap.Format.RGBA8888)
        transition.setBlending(Pixmap.Blending.None)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val alpha = calculateTransitionAlpha(x, y, width, height, type)
                val sourcePixel = source.getPixel(x, y)

                // Apply alpha blending
                val blendedPixel = applyAlpha(sourcePixel, alpha)
                transition.drawPixel(x, y, blendedPixel)
            }
        }

        return transition
    }

    /**
     * Calculates the alpha value for a pixel based on its position and transition type
     * Returns 0.0 (transparent) to 1.0 (fully opaque)
     */
    private fun calculateTransitionAlpha(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        type: TransitionType
    ): Float {
        // Normalize coordinates to 0.0-1.0
        val nx = x.toFloat() / width
        val ny = y.toFloat() / height

        return when (type) {
            // Edge transitions - linear gradient from edge
            TransitionType.EDGE_NORTH -> ny  // Fade in from top
            TransitionType.EDGE_SOUTH -> 1f - ny  // Fade in from bottom
            TransitionType.EDGE_EAST -> 1f - nx  // Fade in from right
            TransitionType.EDGE_WEST -> nx  // Fade in from left

            // Outer corners - diagonal gradient
            TransitionType.CORNER_OUTER_NE -> minOf(ny, 1f - nx)
            TransitionType.CORNER_OUTER_SE -> minOf(1f - ny, 1f - nx)
            TransitionType.CORNER_OUTER_SW -> minOf(1f - ny, nx)
            TransitionType.CORNER_OUTER_NW -> minOf(ny, nx)

            // Inner corners - inverted diagonal for concave corners
            TransitionType.CORNER_INNER_NE -> maxOf(1f - ny, nx)
            TransitionType.CORNER_INNER_SE -> maxOf(ny, nx)
            TransitionType.CORNER_INNER_SW -> maxOf(ny, 1f - nx)
            TransitionType.CORNER_INNER_NW -> maxOf(1f - ny, 1f - nx)
        }
    }

    /**
     * Applies alpha to a pixel
     */
    private fun applyAlpha(pixel: Int, alpha: Float): Int {
        val r = (pixel ushr 24) and 0xFF
        val g = (pixel ushr 16) and 0xFF
        val b = (pixel ushr 8) and 0xFF
        val a = ((pixel and 0xFF) * alpha).toInt().coerceIn(0, 255)

        return (r shl 24) or (g shl 16) or (b shl 8) or a
    }

    /**
     * Generates transitions for all textures in a biome
     */
    fun generateTransitionsForBiome(
        biomeData: TerrainTextureData,
        sourceDir: Path,
        outputDir: Path,
        generateInnerCorners: Boolean = false
    ) {
        println("\n=== Generating transitions for biome: ${biomeData.biomeName} ===")

        biomeData.textureNames.forEach { textureName ->
            val normalizedName = textureName.replace("_", "")
            val sourceFile = sourceDir.resolve("$normalizedName.png")

            if (sourceFile.toFile().exists()) {
                generateTransitions(
                    sourceTexturePath = sourceFile,
                    outputDir = outputDir,
                    textureName = normalizedName,
                    generateInnerCorners = generateInnerCorners
                )
            } else {
                println("Warning: Source texture not found: $sourceFile")
            }
        }

        println("=== Finished transitions for ${biomeData.biomeName} ===\n")
    }
}