package cz.roldy.utils

import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object AtlasBuilder {

    private const val TILE_SIZE = 512
    private const val GUTTER = 2  // pixels to extrude on each side
    private const val TILES_PER_ROW = 8
    private const val PAIRS_COUNT = 32

    private val PADDED_TILE_SIZE = TILE_SIZE + GUTTER * 2

    /**
     * Input: folder with files named like:
     *   albedo_00.png, normal_00.png
     *   albedo_01.png, normal_01.png
     *   ... up to albedo_30.png, normal_30.png
     */
    fun buildAtlas(inputDir: File, outputFile: File) {
        val pairsPerRow = TILES_PER_ROW / 2
        val rowCount = (PAIRS_COUNT + pairsPerRow - 1) / pairsPerRow  // ceil division

        val atlasWidth = TILES_PER_ROW * PADDED_TILE_SIZE
        val atlasHeight = rowCount * PADDED_TILE_SIZE

        println("Atlas size: ${atlasWidth}x${atlasHeight}")

        val atlas = BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB)
        val g = atlas.createGraphics()
        val normals = inputDir.listFiles().filter { it.name.endsWith("_n.png") }.sorted()
        val albedo = inputDir.listFiles().filter { it.name.endsWith("_a.png") }.sorted()
        for (i in 0 until PAIRS_COUNT) {
            val albedoFile = albedo[i]
            val normalFile = normals[i]

            if (!albedoFile.exists() || !normalFile.exists()) {
                println("Warning: Missing texture pair $i")
                continue
            }

            val albedoRaw = ImageIO.read(albedoFile)
            val normalRaw = ImageIO.read(normalFile)

            // Resize to TILE_SIZE
            val albedo = resize(albedoRaw, TILE_SIZE, TILE_SIZE)
            val normal = resize(normalRaw, TILE_SIZE, TILE_SIZE)

            println("Loaded pair $i: albedo=${albedo.width}x${albedo.height}, normal=${normal.width}x${normal.height}")

            val col = i % pairsPerRow
            val row = i / pairsPerRow

            val albedoX = (col * 2) * PADDED_TILE_SIZE
            val albedoY = row * PADDED_TILE_SIZE

            val normalX = (col * 2 + 1) * PADDED_TILE_SIZE
            val normalY = row * PADDED_TILE_SIZE

            drawTileWithGutter(g, albedo, albedoX, albedoY)
            drawTileWithGutter(g, normal, normalX, normalY)

            println("Placed pair $i at row=$row, col=$col")
        }

        g.dispose()
        ImageIO.write(atlas, "PNG", outputFile)
        println("Saved atlas to: ${outputFile.absolutePath}")
    }

    private fun resize(img: BufferedImage, width: Int, height: Int): BufferedImage {
        val resized = BufferedImage(width, height, img.type.takeIf { it != 0 } ?: BufferedImage.TYPE_INT_ARGB)
        val g = resized.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g.drawImage(img, 0, 0, width, height, null)
        g.dispose()
        return resized
    }

    private fun drawTileWithGutter(g: Graphics2D, tile: BufferedImage, x: Int, y: Int) {
        val w = tile.width
        val h = tile.height

        // Draw main tile in center
        g.drawImage(tile, x + GUTTER, y + GUTTER, null)

        // Top gutter - stretch top 1px row
        g.drawImage(
            tile,
            x + GUTTER, y,                          // dest top-left
            x + GUTTER + w, y + GUTTER,             // dest bottom-right
            0, 0,                                   // src top-left
            w, 1,                                   // src bottom-right (top 1px row)
            null
        )

        // Bottom gutter - stretch bottom 1px row
        g.drawImage(
            tile,
            x + GUTTER, y + GUTTER + h,
            x + GUTTER + w, y + GUTTER + h + GUTTER,
            0, h - 1,
            w, h,
            null
        )

        // Left gutter - stretch left 1px column
        g.drawImage(
            tile,
            x, y + GUTTER,
            x + GUTTER, y + GUTTER + h,
            0, 0,
            1, h,
            null
        )

        // Right gutter - stretch right 1px column
        g.drawImage(
            tile,
            x + GUTTER + w, y + GUTTER,
            x + GUTTER + w + GUTTER, y + GUTTER + h,
            w - 1, 0,
            w, h,
            null
        )

        // Top-left corner
        g.drawImage(
            tile,
            x, y,
            x + GUTTER, y + GUTTER,
            0, 0,
            1, 1,
            null
        )

        // Top-right corner
        g.drawImage(
            tile,
            x + GUTTER + w, y,
            x + GUTTER + w + GUTTER, y + GUTTER,
            w - 1, 0,
            w, 1,
            null
        )

        // Bottom-left corner
        g.drawImage(
            tile,
            x, y + GUTTER + h,
            x + GUTTER, y + GUTTER + h + GUTTER,
            0, h - 1,
            1, h,
            null
        )

        // Bottom-right corner
        g.drawImage(
            tile,
            x + GUTTER + w, y + GUTTER + h,
            x + GUTTER + w + GUTTER, y + GUTTER + h + GUTTER,
            w - 1, h - 1,
            w, h,
            null
        )
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val inputDir = File("resources/terrain/synty/combined")   // change this
        val outputFile = File("assets/terrain/terrain2.png")

        buildAtlas(inputDir, outputFile)
    }
}