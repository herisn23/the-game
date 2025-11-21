package org.roldy.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object AtlasExtractor {
    fun extractAtlas(atlas: TextureAtlas, outputDir: String) {
        val regions = atlas.regions

        for (region in regions) {
            extractRegion(region, outputDir)
        }

        atlas.dispose()
    }

    private fun extractRegion(region: TextureAtlas.AtlasRegion, outputDir: String?) {
        val texture = region.getTexture()


        // Get texture data
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare()
        }

        val fullPixmap = texture.getTextureData().consumePixmap()


        // Extract the region
        val x = region.getRegionX()
        val y = region.getRegionY()
        val width = region.getRegionWidth()
        val height = region.getRegionHeight()


        // Create new pixmap for this region
        val regionPixmap = Pixmap(width, height, fullPixmap.getFormat())


        // Copy pixels
        regionPixmap.drawPixmap(
            fullPixmap,
            0, 0,  // destination x, y
            x, y,  // source x, y
            width, height // width, height
        )


        // Save to file
        val fileName = region.name + ".png"
        val outputFile = Gdx.files.local(outputDir + "/" + fileName)

        PixmapIO.writePNG(outputFile, regionPixmap)
        // Cleanup
        regionPixmap.dispose()
        fullPixmap.dispose()
    }

}