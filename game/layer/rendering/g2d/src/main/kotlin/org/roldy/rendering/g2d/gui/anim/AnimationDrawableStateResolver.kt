package org.roldy.rendering.g2d.gui.anim


object Pressed : AnimationDrawableState
object Over : AnimationDrawableState
object Normal : AnimationDrawableState
object Disabled : AnimationDrawableState


interface AnimationDrawableState

interface AnimationDrawableStateResolver {
    val state: AnimationDrawableState
}
