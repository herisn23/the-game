package org.roldy.rendering.g2d.gui

import org.roldy.rendering.g2d.gui.anim.core.AnimationDrawableState


interface UIAnimationState : AnimationDrawableState

object Pressed : UIAnimationState
object Over : UIAnimationState
object Normal : UIAnimationState
object Disabled : UIAnimationState