package org.roldy.rendering.pawn.skeleton.attribute

import org.roldy.rendering.g2d.animation.AnimationType


abstract class PawnAnimationType(override val name: String) : AnimationType(name)

object Idle : PawnAnimationType("idle")
object Slash1H : PawnAnimationType("slash1H")
object WalkU : PawnAnimationType("walkU")
object WalkL : PawnAnimationType("walkL")