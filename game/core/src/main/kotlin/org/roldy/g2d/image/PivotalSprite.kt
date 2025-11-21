package org.roldy.g2d.image

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image

class PivotalImage(
    region: TextureRegion,
    private val pivot: Vector2
) : Image(region) {

    val pivotX = width * pivot.x
    val pivotY = height * pivot.y
    var anchor: Vector2 = Vector2(1f, 1f)
    fun setPivotalPosition(posX: Float, posY: Float) {
        setPosition(
            posX - pivotX * anchor.x,
            posY - pivotY * anchor.y
        )
    }

    fun resetPosition() {
        setPivotalPosition(0f, 0f)
    }

//    override fun setParent(parent: Group?) {
//        super.setParent(parent)
//        resetPosition()
//    }
}