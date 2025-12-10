package org.roldy.rendering.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import org.roldy.rendering.unity.TextureCombiner.CombineMode.*


object TextureCombiner {
    /**
     * Combines albedo, normal, and mask maps into a single texture
     * @param albedoPath - Albedo/diffuse texture
     * @param normalPath - Normal map
     * @param maskPath - Mask map (metallic, roughness, AO, etc.)
     * @param outputPath - Output file path
     * @param mode - Combination mode (see CombineMode enum)
     */
    fun combineTextures(
        albedoPath: String?, normalPath: String?, maskPath: String?,
        outputPath: String?, mode: CombineMode?
    ) {
        val albedo = Pixmap(Gdx.files.absolute(albedoPath))
        val normal = Pixmap(Gdx.files.absolute(normalPath))
        val mask = Pixmap(Gdx.files.absolute(maskPath))


        // Verify dimensions match
        val width = albedo.getWidth()
        val height = albedo.getHeight()

        require(!(normal.getWidth() != width || normal.getHeight() != height || mask.getWidth() != width || mask.getHeight() != height)) { "All textures must have the same dimensions" }

        val combined = Pixmap(width, height, Pixmap.Format.RGBA8888)
        combined.setBlending(Pixmap.Blending.None)

        for (y in 0..<height) {
            for (x in 0..<width) {
                val albedoPixel = albedo.getPixel(x, y)
                val normalPixel = normal.getPixel(x, y)
                val maskPixel = mask.getPixel(x, y)

                val combinedPixel = when (mode) {
                    ALBEDO_RGB_MASK_A -> packAlbedoRGB_MaskA(albedoPixel, maskPixel)
                    ALBEDO_RGB_NORMAL_A -> packAlbedoRGB_NormalA(albedoPixel, normalPixel)
                    NORMAL_RG_MASK_BA -> packNormalRG_MaskBA(normalPixel, maskPixel)
                    BLEND_ALL -> blendAll(albedoPixel, normalPixel, maskPixel)
                    MULTIPLY_MASK -> multiplyMask(albedoPixel, maskPixel)
                    else -> throw IllegalArgumentException("Unexpected mode: $mode")
                }

                combined.drawPixel(x, y, combinedPixel)
            }
        }

        PixmapIO.writePNG(Gdx.files.absolute(outputPath), combined)

        albedo.dispose()
        normal.dispose()
        mask.dispose()
        combined.dispose()

        println("Combined texture saved to: " + outputPath)
    }

    // Pack albedo in RGB, mask's R channel in Alpha
    private fun packAlbedoRGB_MaskA(albedoPixel: Int, maskPixel: Int): Int {
        val ar = (albedoPixel ushr 24) and 0xFF
        val ag = (albedoPixel ushr 16) and 0xFF
        val ab = (albedoPixel ushr 8) and 0xFF
        val mr = (maskPixel ushr 24) and 0xFF

        return (ar shl 24) or (ag shl 16) or (ab shl 8) or mr
    }

    // Pack albedo in RGB, normal's R channel in Alpha
    private fun packAlbedoRGB_NormalA(albedoPixel: Int, normalPixel: Int): Int {
        val ar = (albedoPixel ushr 24) and 0xFF
        val ag = (albedoPixel ushr 16) and 0xFF
        val ab = (albedoPixel ushr 8) and 0xFF
        val nr = (normalPixel ushr 24) and 0xFF

        return (ar shl 24) or (ag shl 16) or (ab shl 8) or nr
    }

    // Pack normal RG channels, mask in BA channels
    private fun packNormalRG_MaskBA(normalPixel: Int, maskPixel: Int): Int {
        val nr = (normalPixel ushr 24) and 0xFF
        val ng = (normalPixel ushr 16) and 0xFF
        val mr = (maskPixel ushr 24) and 0xFF
        val mg = (maskPixel ushr 16) and 0xFF

        return (nr shl 24) or (ng shl 16) or (mr shl 8) or mg
    }

    // Blend all three textures equally
    private fun blendAll(albedoPixel: Int, normalPixel: Int, maskPixel: Int): Int {
        val ar = (albedoPixel ushr 24) and 0xFF
        val ag = (albedoPixel ushr 16) and 0xFF
        val ab = (albedoPixel ushr 8) and 0xFF

        val nr = (normalPixel ushr 24) and 0xFF
        val ng = (normalPixel ushr 16) and 0xFF
        val nb = (normalPixel ushr 8) and 0xFF

        val mr = (maskPixel ushr 24) and 0xFF
        val mg = (maskPixel ushr 16) and 0xFF
        val mb = (maskPixel ushr 8) and 0xFF

        val r = (ar + nr + mr) / 3
        val g = (ag + ng + mg) / 3
        val b = (ab + nb + mb) / 3

        return (r shl 24) or (g shl 16) or (b shl 8) or 0xFF
    }

    // Multiply albedo by mask (useful for AO or shadows)
    private fun multiplyMask(albedoPixel: Int, maskPixel: Int): Int {
        val ar = (albedoPixel ushr 24) and 0xFF
        val ag = (albedoPixel ushr 16) and 0xFF
        val ab = (albedoPixel ushr 8) and 0xFF

        val mr = (maskPixel ushr 24) and 0xFF
        val mg = (maskPixel ushr 16) and 0xFF
        val mb = (maskPixel ushr 8) and 0xFF

        val r = (ar * mr) / 255
        val g = (ag * mg) / 255
        val b = (ab * mb) / 255

        return (r shl 24) or (g shl 16) or (b shl 8) or 0xFF
    }

    enum class CombineMode {
        ALBEDO_RGB_MASK_A,  // Albedo in RGB, Mask R in Alpha
        ALBEDO_RGB_NORMAL_A,  // Albedo in RGB, Normal R in Alpha
        NORMAL_RG_MASK_BA,  // Normal RG, Mask RG in BA
        BLEND_ALL,  // Average blend of all three
        MULTIPLY_MASK // Albedo multiplied by mask
    }
}