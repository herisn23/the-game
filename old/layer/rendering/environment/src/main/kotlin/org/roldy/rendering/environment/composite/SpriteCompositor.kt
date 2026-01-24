package org.roldy.rendering.environment.composite

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.rendering.environment.harvestable.MapAtlas
import org.roldy.rendering.g2d.Pivot
import org.roldy.rendering.g2d.PivotDefaults


@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class AddComposition

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class CompositeChain


enum class CompositeAnimationType {
    Scale
}

data class CompositeAnimation(
    val type: CompositeAnimationType,
    val scaleX: Float,
    val scaleY: Float,
    val speed: Float
)

data class CompositeSprite(
    val sprite: Sprite,
    val enabled: () -> Boolean,
    val animation: CompositeAnimation? = null
)

class SpriteCompositor(
    val pool: SpritePool,
    val atlas: MapAtlas,
    val tileSize: Float
) {

    private val sprites: MutableList<CompositeSprite> = mutableListOf()

    @AddComposition
    fun texture(
        position: Vector2,
        region: MapAtlas.() -> TextureRegion,
        visible: () -> Boolean,
        animation: CompositeAnimation? = null,
        configure: Pivot.() -> Unit = {}
    ): Sprite =
        atlas
            .region()
            .run {
                val texture = this
                val config = PivotDefaults(
                    position.x,
                    position.y,
                    1f,
                    1f,
                    regionWidth.toFloat(),
                    regionHeight.toFloat(),
                    tileSize, tileSize
                ).pivot().apply(configure)
                pool.obtain().apply {
                    setRegion(texture)
                    setSize(config.width, config.height)
                    setPosition(config.x, config.y)
                    setScale(config.scaleX, config.scaleY)
                }
            }
            .also {
                sprites.add(CompositeSprite(it, visible, animation))
            }


    @CompositeChain
    fun Pivot.offset(x: Float, y: Float) =
        apply {
            this.x += x// * (parent.parentWidth / 2) - width / 2
            this.y += y// * (parent.parentHeight / 2) - height / 2
        }


    @CompositeChain
    fun Pivot.centered() =
        center().parent().center().pivot()


    fun retrieve(): List<CompositeSprite> = sprites.toList().also { sprites.clear() }
}