package org.roldy.core.renderer.drawable

import com.badlogic.gdx.utils.Pool

class DrawablePool(
    private val instance: () -> Drawable
) : Pool<Drawable>() {
    override fun newObject(): Drawable {
        return instance()
    }
}