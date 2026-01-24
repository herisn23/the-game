package org.roldy.g3d.pawn

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model

object PawnAssetManager2 {
    val assetManager by lazy {
        AssetManager().apply {
            load("3d/pawn/Pawn.g3db", Model::class.java)
            load("3d/pawn/animation/Idle.g3db", Model::class.java)
            load("3d/pawn/animation/Idle2.g3db", Model::class.java)
            load("3d/pawn/animation/Walking.g3db", Model::class.java)
            load("3d/pawn/PolygonFantasyHero_Texture_Mask_01.png", Texture::class.java)
            load("3d/pawn/PolygonFantasyHero_Texture_Mask_02.png", Texture::class.java)
            load("3d/pawn/PolygonFantasyHero_Texture_Mask_03.png", Texture::class.java)
            load("3d/pawn/PolygonFantasyHero_Texture_Mask_04.png", Texture::class.java)
            load("3d/pawn/PolygonFantasyHero_Texture_Mask_05.png", Texture::class.java)
        }
    }
}