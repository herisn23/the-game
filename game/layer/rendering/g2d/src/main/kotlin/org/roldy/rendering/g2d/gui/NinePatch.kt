package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

data class NinePatchParams(
    val left: Int = 0,
    val right: Int = 0,
    val top: Int = 0,
    val bottom: Int = 0,
)

@NinePatchDsl
context(_: C)
fun <A, C : KContext> ninePatch(
    region: TextureRegion,
    params: NinePatchParams,
    element: @NinePatchDsl NinePatchDrawable.() -> A
): A =
    ninePatch(region, params.left, params.right, params.top, params.bottom, element)

@NinePatchDsl
context(_: C)
fun <A, C : KContext> ninePatch(
    region: TextureRegion,
    left: Int = 0,
    right: Int = 0,
    top: Int = 0,
    bottom: Int = 0,
    element: @NinePatchDsl NinePatchDrawable.() -> A
): A {
    val ninePatch = NinePatch(region, left, right, top, bottom)
    val drawable = NinePatchDrawable(ninePatch)
    return element(drawable)
}