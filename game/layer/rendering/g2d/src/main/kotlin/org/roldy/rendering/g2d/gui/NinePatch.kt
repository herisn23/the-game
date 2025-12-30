package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class NinePatchDsl

data class NinePatchParams(
    val left: Int = 0,
    val right: Int = 0,
    val top: Int = 0,
    val bottom: Int = 0,
    val flipX: Boolean = false,
    val flipY: Boolean = false
)

@NinePatchDsl
context(_: C)
fun <A, C : UIContext> ninePatch(
    region: TextureRegion,
    params: NinePatchParams,
    element: @NinePatchDsl NinePatchDrawable.() -> A
): A =
    ninePatch(region, params.left, params.right, params.top, params.bottom, element)

@NinePatchDsl
context(_: C)
fun <C : UIContext> ninePatch(
    region: TextureRegion,
    params: NinePatchParams
): NinePatchDrawable =
    ninePatch(region, params.left, params.right, params.top, params.bottom)

@NinePatchDsl
context(_: C)
fun <A, C : UIContext> ninePatch(
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

@NinePatchDsl
context(_: C)
fun <C : UIContext> ninePatch(
    region: TextureRegion,
    left: Int = 0,
    right: Int = 0,
    top: Int = 0,
    bottom: Int = 0
): NinePatchDrawable {
    val ninePatch = NinePatch(region, left, right, top, bottom)
    return NinePatchDrawable(ninePatch)
}

@NinePatchDsl
fun ninePatchParams(
    clip: Int = 0,
    left: Int = clip,
    right: Int = clip,
    top: Int = clip,
    bottom: Int = clip,
) =
    NinePatchParams(left, right, top, bottom)