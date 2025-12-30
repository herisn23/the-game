package org.roldy.rendering.g2d.gui.anim.core

interface AnimationDrawableState

interface AnimationDrawableStateResolver<S: AnimationDrawableState> {
    val state: S
}
