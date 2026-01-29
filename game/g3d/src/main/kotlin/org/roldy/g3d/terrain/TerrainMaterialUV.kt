package org.roldy.g3d.terrain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

data class TerrainMaterialUV(
    val offset: Vector2,
    val scale: Vector2
)

fun generateTerrainMaterialUVs(
    tileSize: Int,          // 512
    atlasWidth: Int,        // 4096 (original, before padding)
    atlasHeight: Int,       // 2048 (original, before padding)
    materialCount: Int,     // 28
    padding: Int            // 16
): List<TerrainMaterialUV> {
    val tilesPerRow = atlasWidth / tileSize   // 4096/512 = 8
    val tilesPerCol = atlasHeight / tileSize  // 2048/512 = 4

    val paddedTileSize = tileSize + padding * 2  // 512 + 32 = 544
    val paddedAtlasWidth = tilesPerRow * paddedTileSize   // 8 * 544 = 4352
    val paddedAtlasHeight = tilesPerCol * paddedTileSize  // 4 * 544 = 2176

    Gdx.app.log("UV", "Padded atlas: ${paddedAtlasWidth}x${paddedAtlasHeight}")

    val list = ArrayList<TerrainMaterialUV>()

    for (i in 0 until materialCount) {
        val row = i / tilesPerRow
        val col = i % tilesPerRow

        // Offset skips padding to start at tile content
        val offsetX = (col * paddedTileSize + padding).toFloat() / paddedAtlasWidth
        val offsetY = (row * paddedTileSize + padding).toFloat() / paddedAtlasHeight

        // Scale covers only tile content
        val scaleX = tileSize.toFloat() / paddedAtlasWidth
        val scaleY = tileSize.toFloat() / paddedAtlasHeight

        list += TerrainMaterialUV(Vector2(offsetX, offsetY), Vector2(scaleX, scaleY))
    }

    // Debug
    list.take(4).forEachIndexed { i, uv ->
        Gdx.app.log(
            "UV",
            "materialUVs[$i] = offset(${uv.offset.x}, ${uv.offset.y}), scale(${uv.scale.x}, ${uv.scale.y})"
        )
    }

    return list
}