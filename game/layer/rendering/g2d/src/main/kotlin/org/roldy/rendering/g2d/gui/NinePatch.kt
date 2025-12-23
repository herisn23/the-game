package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

data class NinePatchParams(
    val left: Int = 0,
    val right: Int = 0,
    val top: Int = 0,
    val bottom: Int = 0,
)

@Scene2dDsl
context(_: C)
fun <A : Actor, S, C : KContext> KWidget<S>.ninePatch(
    region: TextureRegion,
    params: NinePatchParams,
    element: NinePatchDrawable.() -> A
): A =
    ninePatch(region, params.left, params.right, params.top, params.bottom, element)

@Scene2dDsl
context(_: C)
fun <A : Actor, S, C : KContext> KWidget<S>.ninePatch(
    region: TextureRegion,
    left: Int = 0,
    right: Int = 0,
    top: Int = 0,
    bottom: Int = 0,
    element: (NinePatchDrawable).() -> A
): A {
    val ninePatch = NinePatch(region, left, right, top, bottom)
    val drawable = NinePatchDrawable(ninePatch)
    return actor(element(drawable))
}