package org.roldy.g3d.terrain

import com.badlogic.gdx.math.Vector4


object AlternatingAtlasUV {
    private const val TILE_SIZE = 512
    private const val GUTTER = 2
    private const val PADDED_TILE_SIZE = TILE_SIZE + GUTTER * 2
    private const val TILES_PER_ROW = 8
    private const val MATERIAL_COUNT = 32
    /**
     * Calculate UV data for alternating A/N layout:
     * | A0 | N0 | A1 | N1 | A2 | N2 | A3 | N3 |
     * | A4 | N4 | ...
     *
     * @param textureIndex 0-31
     * @param tilesPerRow 8 (4 pairs)
     * @return Vector4(albedoU, albedoV, tileWidth, tileHeight)
     */
    fun getUVData(textureIndex: Int): Vector4 {
        val pairsPerRow = TILES_PER_ROW / 2
        val rowCount = 8

        val atlasWidth = TILES_PER_ROW * PADDED_TILE_SIZE   // 4128
        val atlasHeight = rowCount * PADDED_TILE_SIZE       // 4128

        val col = textureIndex % pairsPerRow
        val row = textureIndex / pairsPerRow

        val albedoCol = col * 2

        // UV for the INNER tile (skip gutter)
        val u = (albedoCol * PADDED_TILE_SIZE + GUTTER).toFloat() / atlasWidth
        val v = (row * PADDED_TILE_SIZE + GUTTER).toFloat() / atlasHeight

        val tileU = TILE_SIZE.toFloat() / atlasWidth
        val tileV = TILE_SIZE.toFloat() / atlasHeight

        return Vector4(u, v, tileU, tileV)
    }

    // For normal map offset in shader
    fun getPaddedTileWidth(): Float {
        val atlasWidth = TILES_PER_ROW * PADDED_TILE_SIZE
        return PADDED_TILE_SIZE.toFloat() / atlasWidth
    }

    // Generate all x UV entries
    fun generateAllUVs(): List<Vector4> =
        (0..<MATERIAL_COUNT).map {
            getUVData(it)
        }
}