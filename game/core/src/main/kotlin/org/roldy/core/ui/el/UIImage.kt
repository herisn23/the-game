package org.roldy.core.ui.el

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.ui.Scene2dDsl
import org.roldy.core.ui.UIContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Scene2dDsl
class UIImage : Image {
    constructor(patch: Drawable): super(patch)
    constructor(region: TextureRegion): super(region)
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.image(
    drawable: Drawable,
    init: context(C) (@Scene2dDsl UIImage).(S) -> Unit = {}
): UIImage {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UIImage(drawable), init)
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.image(
    region: TextureRegion,
    init: context(C) (@Scene2dDsl UIImage).(S) -> Unit = {}
): UIImage {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(UIImage(region), init)
}