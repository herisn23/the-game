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
    val tileDecorationDesertAtlas: TextureAtlas = AtlasLoader.tileDecorationDesert.disposable()

    inline fun <reified T : TileDecoration> region(crossinline choose: T.() -> String): TextureRegion =
        when (T::class) {
            TileDecorationCold::class -> cold { (this as T).choose() }
            TileDecorationNormal::class -> normal { (this as T).choose() }
            TileDecorationTropic::class -> tropic { (this as T).choose() }
            TileDecorationDesert::class -> desert { (this as T).choose() }
            else -> error("Unknown tile decoration type: ${T::class.qualifiedName}")
        }

    fun normal(texture: TileDecorationNormal.() -> String): TextureAtlas.AtlasRegion =
        tileDecorationNormalAtlas[TileDecorationNormal.texture()]

    fun cold(texture: TileDecorationCold.() -> String) =
        tileDecorationColdAtlas[TileDecorationCold.texture()]

    fun tropic(texture: TileDecorationTropic.() -> String) =
        tileDecorationTropicAtlas[TileDecorationTropic.texture()]

    fun desert(texture: TileDecorationDesert.() -> String) =
        tileDecorationDesertAtlas[TileDecorationDesert.texture()]
}