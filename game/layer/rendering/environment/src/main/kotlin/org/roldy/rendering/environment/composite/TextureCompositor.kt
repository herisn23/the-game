package org.roldy.rendering.environment.composite

import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.x
import org.roldy.rendering.environment.TileDecorationAtlas
import org.roldy.rendering.g2d.Pivot
import org.roldy.rendering.g2d.PivotDefaults


@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class AddComposition

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class CompositeChain


class TextureCompositor(
    val atlas: TileDecorationAtlas,
    val tileSize: Float
) {

    private val textures: MutableList<CompositeTexture> = mutableListOf()


    @AddComposition
    fun texture(
        region: TileDecorationAtlas.() -> TextureRegion,
        configure: Pivot.() -> Unit = {}
    ) =
        atlas.region().run {
            val config = PivotDefaults(
                0f,
                0f,
                1f,
                1f,
                regionWidth.toFloat(),
                regionHeight.toFloat(),
                tileSize, tileSize
            ).pivot().apply(configure)
            CompositeTexture(config.x x config.y, this)
        }.also(textures::add)


    @CompositeChain
    fun Pivot.offset(x: Float, y: Float) =
        apply {
            this.x += x
            this.y += y
        }


    fun retrieve(): List<CompositeTexture> = textures
}