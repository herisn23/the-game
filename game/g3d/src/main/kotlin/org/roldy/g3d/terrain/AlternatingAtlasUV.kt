package org.roldy.g3d.terrain

import com.badlogic.gdx.math.Vector4


object AlternatingAtlasUV {
    /**
     * Calculate UV data for alternating A/N layout:
     * | A0 | N0 | A1 | N1 | A2 | N2 | A3 | N3 |
     * | A4 | N4 | ...
     *
     * @param textureIndex 0-31
     * @param tilesPerRow 8 (4 pairs)
     * @return Vector4(albedoU, albedoV, tileWidth, tileHeight)
     */
    fun getUVData(textureIndex: Int, tilesPerRow: Int): Vector4 {
        val pairsPerRow = tilesPerRow / 2 // 4 pairs per row

        val col = textureIndex % pairsPerRow // 0-3
        val row = textureIndex / pairsPerRow // 0-7


        // Albedo is at even columns (0, 2, 4, 6)
        val albedoCol = col * 2

        val tileU = 1.0f / tilesPerRow // 1/8 = 0.125
        val tileV = 1.0f / (32 / pairsPerRow) // 1/8 = 0.125

        val u = albedoCol * tileU
        val v = row * tileV

        return Vector4(u, v, tileU, tileV)
    }

    // Generate all x UV entries
    fun generateAllUVs(count: Int): List<Vector4> =
        (0..<count).map {
            getUVData(it, 8)
        }
}