package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import org.roldy.core.utils.hex
import org.roldy.g3d.pawn.utils.copyAnimation
import org.roldy.g3d.pawn.utils.findNode

class PawnConfiguration(
    model: Model,
    val maskTextures: MaskTextures
) {
    val parts = listOf(
        "Root",
        "Chr_Hips_Male_00",
        "Chr_HandLeft_Male_00",
        "Chr_HandRight_Male_00",
        "Chr_LegLeft_Male_00",
        "Chr_LegRight_Male_00",
        "Chr_ArmLowerLeft_Male_00",
        "Chr_ArmLowerRight_Male_11",
//            "Chr_ArmLowerRight_Male_00",
        "Chr_ArmUpperLeft_Male_00",
        "Chr_ArmUpperRight_Male_00",
        "Chr_Torso_Male_00",
//        "Chr_FacialHair_Male_01",
//        "Chr_Eyebrow_Male_01",
        "Chr_Head_Male_19",
//        "Chr_HeadCoverings_No_Hair_09"
    )

    val instance: ModelInstance = ModelInstance(model.apply {
        //show only specific parts
        val nodes = this.nodes.toList()
        this.nodes.clear()
        parts.forEach {
            val node = nodes.findNode(it)
            this.nodes.add(node)
        }
        PawnAnimations.all.forEach { (id, anim) ->
            animations.add(copyAnimation(anim.model.get().animations.first(), id))
        }
    })

    var colorPrimary = hex("7B6A5A")
    var colorSecondary = hex("BCBCBC")
    var colorLeatherPrimary = hex("4F3729")
    var colorLeatherSecondary = hex("8E9F99")
    var colorMetalPrimary = hex("5F5447")
    var colorMetalSecondary = hex("2A3845")
    var colorMetalDark = hex("66675B")
    var colorHair = hex("623C0D")
    var colorSkin = hex("D1A275")
    var colorStubble = hex("A89276")
    var colorScar = hex("B28B66")
    var colorBodyArt = hex("299F2A")
    var colorEyes = hex("000000")
    var bodyArtAmount = 1f
}

class MaskTextures {
    val mask1: Texture by lazy { PawnAssetManager.mask1.get().let(::setupTexture) }
    val mask2: Texture by lazy { PawnAssetManager.mask2.get().let(::setupTexture) }
    val mask3: Texture by lazy { PawnAssetManager.mask3.get().let(::setupTexture) }
    val mask4: Texture by lazy { PawnAssetManager.mask4.get().let(::setupTexture) }
    val mask5: Texture by lazy { PawnAssetManager.mask5.get().let(::setupTexture) }
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

