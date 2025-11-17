package org.roldy.example

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport


data class TextureComponent(val region: TextureRegion) : Component

class PositionComponent : Component {
    var x: Float = 0f
    var y: Float = 0f
}

class RenderSystem(
    private val batch: SpriteBatch,
    private val viewport: Viewport
): IteratingSystem(
    Family.all(
        TextureComponent::class.java,
        PositionComponent::class.java
    ).get()) {
    private val tm: ComponentMapper<TextureComponent> =
        ComponentMapper.getFor(TextureComponent::class.java)

    private val pm: ComponentMapper<PositionComponent> =
        ComponentMapper.getFor(PositionComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tex = tm.get(entity)
        val pos = pm.get(entity)
        batch.draw(tex.region, pos.x, pos.y, viewport.worldWidth, viewport.worldHeight)
    }
}