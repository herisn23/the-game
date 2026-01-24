package org.roldy.core.ui.anim.base

interface AnimationDrawableState

interface AnimationDrawableStateResolver<S: AnimationDrawableState> {
    val state: S
}
