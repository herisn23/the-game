package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.rendering.environment.TileBehaviourAdapter
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.composite.CompositeSprite
import org.roldy.rendering.g2d.Pivot
import org.roldy.rendering.g2d.PivotDefaults
import org.roldy.rendering.g2d.animation.state.Animator
import org.roldy.rendering.g2d.animation.state.animation
import org.roldy.rendering.g2d.animation.state.float
import org.roldy.rendering.g2d.animation.state.stretch
import kotlin.random.Random

class HarvestableTileBehaviour : TileBehaviourAdapter<HarvestableTileBehaviour.Data>() {
    data class Data(
        override val position: Vector2,
        override val coords: Vector2Int,
        override val data: Map<String, Any> = emptyMap(),
        val icon: TextureRegion,
        val sprites: List<CompositeSprite>,
        val tileSize: Float,
        val onReset: Data.() -> Unit
    ) : TileObject.Data

    data class SpriteAnimation(
        val comp: CompositeSprite,
        val animator: Animator? = null,
    )

    var animations: List<SpriteAnimation> = emptyList()

    var iconAnimator: Animator? = null
    var iconPivot: Pivot? = null

    context(delta: Float, camera: Camera)
    override fun draw(
        data: Data,
        batch: SpriteBatch
    ) {
        animations.forEach { (comp, anim) ->
            if (!comp.enabled()) return@forEach
            if (anim == null) {
                comp.sprite.draw(batch)
            } else {
                anim.update()
                comp.sprite.setScale(anim.data.scaleX, anim.data.scaleY)
                comp.sprite.draw(batch)
            }
        }
        val animator = this.iconAnimator
        val pivot = this.iconPivot
        if (animator != null && pivot != null && false) {
            animator.update()
            animator.render(
                data.icon,
                batch,
                pivot
                    .setScale(animator.data.scaleX, animator.data.scaleY)
                    .setPosition(data.position.x, data.position.y)
                    .center()
                    .parent()
                    .top()
                    .pivot()
            )
        }
    }


    override fun configure(data: Data) {
        val scale = .3f
        iconPivot = PivotDefaults(
            data.position.x,
            data.position.y,
            1f,
            1f,
            data.icon.regionWidth.toFloat() * scale,
            data.icon.regionHeight.toFloat() * scale,
            data.tileSize,
            data.tileSize,
        ).pivot()

        animations = data.sprites.map { comp ->
            val anim: Animator? = let {
                if (comp.animation != null) {
                    animation {
                        scale(
                            comp.animation.speed,
                            0f,
                            stretch(
                                comp.sprite.scaleX,
                                comp.sprite.scaleX - comp.animation.scaleX,
                                comp.sprite.scaleY,
                                comp.sprite.scaleY + comp.animation.scaleY
                            )
                        )
                    }
                } else {
                    null
                }
            }
            SpriteAnimation(comp, anim)
        }

        iconAnimator = animation {
            vertical(2f, Random.nextFloat() * Random.nextInt(0, 1000), float(.3f))
        }


    }

    override fun reset() {
        super.reset()
        animations = emptyList()
        iconAnimator = null
        iconPivot = null
        data?.let {
            it.onReset(it)
        }
    }

}
