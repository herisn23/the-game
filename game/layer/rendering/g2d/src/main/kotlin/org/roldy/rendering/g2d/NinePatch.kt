package org.roldy.rendering.g2d

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

data class NinePatchParams(
    val left: Int = 0,
    val right: Int = 0,
    val top: Int = 0,
    val bottom: Int = 0,
)

fun <D> ninePatch(
    region: TextureRegion,
    params: NinePatchParams,
    configure: NinePatchDrawable.() -> D
): D {
    return ninePatch(region, params.left, params.right, params.top, params.bottom, configure)
}

fun <D> ninePatch(
    region: TextureRegion,
    left: Int = 0,
    right: Int = 0,
    top: Int = 0,
    bottom: Int = 0,
    configure: NinePatchDrawable.() -> D
): D {
    val ninePatch = NinePatch(region, left, right, top, bottom)
    val drawable = NinePatchDrawable(ninePatch)
    return configure(drawable)
}