package org.roldy.g2d.sprite

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class PivotalSprite(
    region: TextureRegion,
    private val pivot: Vector2
) : Sprite(region) {

    val pivotX = width * pivot.x
    val pivotY = height * pivot.y

    fun setPivotalPosition(posX: Float, posY: Float) {
        setPosition(
            posX - pivotX,
            posY - pivotY
        )
    }

}