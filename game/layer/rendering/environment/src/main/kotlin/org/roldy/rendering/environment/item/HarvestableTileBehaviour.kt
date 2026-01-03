package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.rendering.environment.TileBehaviourAdapter
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.composite.CompositeTexture
import org.roldy.rendering.g2d.Pivot
import org.roldy.rendering.g2d.PivotDefaults
import org.roldy.rendering.g2d.animation.state.Animator
import org.roldy.rendering.g2d.animation.state.animation
import org.roldy.rendering.g2d.animation.state.float
import kotlin.random.Random

class HarvestableTileBehaviour : TileBehaviourAdapter<HarvestableTileBehaviour.Data>() {
    data class Data(
        override val position: Vector2,
        override val coords: Vector2Int,
        override val data: Map<String, Any> = emptyMap(),
        val icon: TextureRegion,
        val textures: List<CompositeTexture>,
        val tileSize: Float,
    ) : TileObject.Data


    var animator: Animator? = null
    var pivot: Pivot? = null

    context(delta: Float, camera: Camera)
    override fun draw(
        data: Data,
        batch: SpriteBatch
    ) {
        data.textures.forEach { (position, texture) ->
            batch.draw(texture, data.position.x + position.x, data.position.y + position.y)
        }
        val animator = this.animator
        val pivot = this.pivot
        if (animator != null && pivot != null) {
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
        pivot = PivotDefaults(
            data.position.x,
            data.position.y,
            1f,
            1f,
            data.icon.regionWidth.toFloat() * scale,
            data.icon.regionHeight.toFloat() * scale,
            data.tileSize,
            data.tileSize,
        ).pivot()

        animator = animation {
            vertical(2f, Random.nextFloat() * Random.nextInt(0, 1000), float(.3f))
        }
    }

    override fun reset() {
        super.reset()
        animator = null
        pivot = null
    }

}
