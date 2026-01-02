package org.roldy.rendering.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.utils.get
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter

sealed interface TileDecoration

class TileDecorationAtlas(
) : AutoDisposableAdapter() {
    val tileDecorationNormalAtlas: TextureAtlas = AtlasLoader.tileDecorationNormal.disposable()
    val tileDecorationColdAtlas: TextureAtlas = AtlasLoader.tileDecorationCold.disposable()
    val tileDecorationTropicAtlas: TextureAtlas = AtlasLoader.tileDecorationTropic.disposable()

    inline fun <reified T : TileDecoration> region(choose: T.() -> String): TextureRegion =
        when (T::class) {
            TileDecorationCold::class -> tileDecorationColdAtlas[(TileDecorationCold as T).choose()]
            TileDecorationNormal::class -> tileDecorationNormalAtlas[(TileDecorationNormal as T).choose()]
            TileDecorationTropic::class -> tileDecorationTropicAtlas[(TileDecorationTropic as T).choose()]
            else -> error("Unknown tile decoration type: ${T::class.qualifiedName}")
        }
}