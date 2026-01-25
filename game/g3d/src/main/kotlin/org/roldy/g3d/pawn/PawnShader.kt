package org.roldy.g3d.pawn

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider

class PawnShader(
    val configuration: PawnConfiguration,
    renderable: Renderable
) : DefaultShader(renderable, Config().apply {
    fragmentShader = Gdx.files.internal("shaders/character.fragment.glsl").readString()
}) {
    private val maskTextures = MaskTextures()

    override fun render(renderable: Renderable) {
        colorize(renderable)
        super.render(renderable)
    }

    fun colorize(renderable: Renderable) {
        configuration.let { controller ->
            // Base texture - unit 0
            val diffuse = renderable.material.get(TextureAttribute::class.java, TextureAttribute.Diffuse)
            if (diffuse != null) {
                diffuse.textureDescription.texture.bind(0)
                if (program.hasUniform("u_texture"))
                    program.setUniformi("u_texture", 0)
            }

            // Masks - fixed units (always bind to same units)
            maskTextures.mask1.bind(1)
            program.setUniformi("u_mask01", 1)

            maskTextures.mask2.bind(2)
            program.setUniformi("u_mask02", 2)

            maskTextures.mask3.bind(3)
            program.setUniformi("u_mask03", 3)

            maskTextures.mask4.bind(4)
            program.setUniformi("u_mask04", 4)

            maskTextures.mask5.bind(5)
            program.setUniformi("u_mask05", 5)

            // Set all color uniforms
            program.setUniformf(
                "u_colorPrimary",
                controller.colorPrimary.r,
                controller.colorPrimary.g,
                controller.colorPrimary.b
            )
            program.setUniformf(
                "u_colorSecondary",
                controller.colorSecondary.r,
                controller.colorSecondary.g,
                controller.colorSecondary.b
            )
            program.setUniformf(
                "u_colorLeatherPrimary",
                controller.colorLeatherPrimary.r,
                controller.colorLeatherPrimary.g,
                controller.colorLeatherPrimary.b
            )
            program.setUniformf(
                "u_colorLeatherSecondary",
                controller.colorLeatherSecondary.r,
                controller.colorLeatherSecondary.g,
                controller.colorLeatherSecondary.b
            )
            program.setUniformf(
                "u_colorMetalPrimary",
                controller.colorMetalPrimary.r,
                controller.colorMetalPrimary.g,
                controller.colorMetalPrimary.b
            )
            program.setUniformf(
                "u_colorMetalSecondary",
                controller.colorMetalSecondary.r,
                controller.colorMetalSecondary.g,
                controller.colorMetalSecondary.b
            )
            program.setUniformf(
                "u_colorMetalDark",
                controller.colorMetalDark.r,
                controller.colorMetalDark.g,
                controller.colorMetalDark.b
            )
            program.setUniformf(
                "u_colorHair",
                controller.colorHair.r,
                controller.colorHair.g,
                controller.colorHair.b
            )
            program.setUniformf(
                "u_colorSkin",
                controller.colorSkin.r,
                controller.colorSkin.g,
                controller.colorSkin.b
            )
            program.setUniformf(
                "u_colorStubble",
                controller.colorStubble.r,
                controller.colorStubble.g,
                controller.colorStubble.b
            )
            program.setUniformf(
                "u_colorScar",
                controller.colorScar.r,
                controller.colorScar.g,
                controller.colorScar.b
            )
            program.setUniformf(
                "u_colorBodyArt",
                controller.colorBodyArt.r,
                controller.colorBodyArt.g,
                controller.colorBodyArt.b
            )
            program.setUniformf("u_bodyArtAmount", controller.bodyArtAmount)
            program.setUniformf(
                "u_colorEyes",
                controller.colorEyes.r,
                controller.colorEyes.g,
                controller.colorEyes.b
            )
        }

        // Set lighting uniforms
        program.setUniformf("u_ambientLight", 0.4f, 0.4f, 0.4f)
        program.setUniformf("u_dirLightColor", 0.8f, 0.8f, 0.8f)
        program.setUniformf("u_dirLightDir", -1f, -0.8f, -0.2f)

        // Render the mesh
        super.render(renderable)
    }

    private class MaskTextures {
        val mask1: Texture = PawnAssetManager.mask1.get().let(::setupTexture)
        val mask2: Texture = PawnAssetManager.mask2.get().let(::setupTexture)
        val mask3: Texture = PawnAssetManager.mask3.get().let(::setupTexture)
        val mask4: Texture = PawnAssetManager.mask4.get().let(::setupTexture)
        val mask5: Texture = PawnAssetManager.mask5.get().let(::setupTexture)

        fun setupTexture(tex: Texture): Texture {
            // Flip texture vertically
            if (!tex.textureData.isPrepared) {
                tex.textureData.prepare()
            }
            val pixmap = tex.textureData.consumePixmap()

            // Flip Y
            val flipped = Pixmap(pixmap.width, pixmap.height, pixmap.format)
            for (y in 0 until pixmap.height) {
                for (x in 0 until pixmap.width) {
                    flipped.drawPixel(x, pixmap.height - 1 - y, pixmap.getPixel(x, y))
                }
            }

            val flippedTex = Texture(flipped)
            flippedTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
            flippedTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

            pixmap.dispose()
            flipped.dispose()
            return flippedTex
        }
    }


}

class PawnShaderProvider(
    private val configuration: PawnConfiguration,
) : BaseShaderProvider() {

    override fun createShader(renderable: Renderable): Shader {
        return PawnShader(configuration, renderable)
    }
}