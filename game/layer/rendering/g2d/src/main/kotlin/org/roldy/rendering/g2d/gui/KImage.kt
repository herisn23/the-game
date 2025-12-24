package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Scene2dDsl
class KImage : Image {
    constructor(patch: Drawable): super(patch)
    constructor(region: TextureRegion): super(region)
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.image(
    patch: Drawable,
    init: context(C) (@Scene2dDsl KImage).(S) -> Unit = {}
): KImage {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KImage(patch), init)
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.image(
    region: TextureRegion,
    init: context(C) (@Scene2dDsl KImage).(S) -> Unit = {}
): KImage {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KImage(region), init)
}