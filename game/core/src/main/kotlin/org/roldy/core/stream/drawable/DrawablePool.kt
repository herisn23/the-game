package org.roldy.core.stream.drawable

import com.badlogic.gdx.utils.Pool

class DrawablePool(
    private val instance: () -> Drawable
) : Pool<Drawable>() {
    override fun newObject(): Drawable {
        return instance()
    }
}