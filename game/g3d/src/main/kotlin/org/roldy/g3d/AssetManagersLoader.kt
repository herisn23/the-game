package org.roldy.g3d

import org.roldy.g3d.environment.EnvTexturesAssetAssetManager
import org.roldy.g3d.environment.TropicalAssetManager
import org.roldy.g3d.pawn.PawnAssetManager

object AssetManagersLoader {

    val loaders by lazy {
        listOf(
            PawnAssetManager,
            TropicalAssetManager,
            EnvTexturesAssetAssetManager
        )
    }

    fun update() =
        loaders.all { it.assetManager.update() }

}