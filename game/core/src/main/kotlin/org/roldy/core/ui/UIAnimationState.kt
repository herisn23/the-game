package org.roldy.core.ui

import org.roldy.core.ui.anim.base.AnimationDrawableState


interface UIAnimationState : AnimationDrawableState

object Pressed : UIAnimationState
object Over : UIAnimationState
object Normal : UIAnimationState
object Disabled : UIAnimationState