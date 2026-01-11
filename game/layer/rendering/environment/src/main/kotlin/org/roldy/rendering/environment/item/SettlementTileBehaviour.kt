package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.data.state.SettlementState
import org.roldy.rendering.environment.TileBehaviourAdapter
import org.roldy.rendering.environment.TileObject

class SettlementTileBehaviour : TileBehaviourAdapter<SettlementTileBehaviour.Data>() {
    val settlementObject = SpriteTileBehaviour()
    val snowObject = SpriteTileBehaviour()

    override val zIndex: Float get() = settlementObject.zIndex
    override val layer: Int get() = settlementObject.layer

    data class Data(
        val base: SpriteTileBehaviour.Data,
        val snow: SpriteTileBehaviour.Data,
        override val data: Map<String, Any> = emptyMap(),
        val hasSnow: Boolean = false,
        val state: SettlementState,
        val worldPosition: (Vector2Int) -> Vector2
    ) : TileObject.Data by base

    context(delta: Float, camera: Camera)
    override fun draw(
        data: Data,
        batch: SpriteBatch
    ) {

        settlementObject.draw(batch)
        if (data.hasSnow)
            snowObject.draw(batch)
    }

    override fun configure(data: Data) {
        settlementObject.bind(data.base)
        snowObject.bind(data.snow)
    }


    override fun reset() {
        super.reset()
        settlementObject.reset()
    }
}