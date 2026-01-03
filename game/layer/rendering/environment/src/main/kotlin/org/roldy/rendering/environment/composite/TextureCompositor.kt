package org.roldy.rendering.environment.composite

import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.x
import org.roldy.rendering.environment.harvestable.MapAtlas
import org.roldy.rendering.g2d.Pivot
import org.roldy.rendering.g2d.PivotDefaults


@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class AddComposition

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class CompositeChain


class TextureCompositor(
    val atlas: MapAtlas,
    val tileSize: Float
) {

    private val textures: MutableList<CompositeTexture> = mutableListOf()

    @AddComposition
    fun texture(
        region: MapAtlas.() -> TextureRegion,
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
            this.x += x// * (parent.parentWidth / 2) - width / 2
            this.y += y// * (parent.parentHeight / 2) - height / 2
        }


    @CompositeChain
    fun Pivot.centered() =
        center().parent().center().pivot()


    fun retrieve(): List<CompositeTexture> = textures
}