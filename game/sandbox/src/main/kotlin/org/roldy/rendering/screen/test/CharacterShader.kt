package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider

class CharacterShader(
    val characterController: CharacterController,
    renderable: Renderable
) : DefaultShader(renderable, Config().apply {
    fragmentShader = Gdx.files.internal("shaders/character.fragment.glsl").readString()
}) {

    override fun render(renderable: Renderable) {
        colorize2(renderable)
        super.render(renderable)
    }

    fun colorize(renderable: Renderable) {
        characterController.let { controller ->
            var index = 1
            controller.maskTextures.mask01.bind(index)
            program.setUniformi("u_mask01", index++)
//            controller.maskTextures.mask02.bind(index)
//            program.setUniformi("u_mask02", index++)
//            controller.maskTextures.mask03.bind(index)
//            program.setUniformi("u_mask03", index++)
//            controller.maskTextures.mask04.bind(index)
//            program.setUniformi("u_mask04", index++)
        }
    }

    fun colorize2(renderable: Renderable) {
        characterController.let { controller ->
            // Base texture - unit 0
            val diffuse = renderable.material.get(TextureAttribute::class.java, TextureAttribute.Diffuse)
            if (diffuse != null) {
                diffuse.textureDescription.texture.bind(0)
                if (program.hasUniform("u_texture"))
                    program.setUniformi("u_texture", 0)
            }

            // Masks - fixed units (always bind to same units)
            controller.maskTextures.mask01.bind(1)
            program.setUniformi("u_mask01", 1)

            controller.maskTextures.mask02.bind(2)
            program.setUniformi("u_mask02", 2)

            controller.maskTextures.mask03.bind(3)
            program.setUniformi("u_mask03", 3)

            controller.maskTextures.mask04.bind(4)
            program.setUniformi("u_mask04", 4)

            controller.maskTextures.mask05.bind(5)
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
}

class CharacterShaderProvider(
    private val characterController: CharacterController,
    val camera: Camera,
) : BaseShaderProvider() {


    override fun createShader(renderable: Renderable): Shader {
        return CharacterShader(characterController, renderable)
    }
}