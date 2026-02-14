package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.g3d.Model
import org.roldy.core.asset.Asset
import org.roldy.core.asset.AssetManagerLoader

interface EnvironmentAssetManagerLoader : AssetManagerLoader {
    val modelMap: Map<String, Asset<Model>>
    val collisionMap: Map<String, Asset<Model>>
}