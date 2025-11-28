package org.roldy.pawn.skeleton.attribute

import org.roldy.core.animation.AnimationType

abstract class PawnAnimationType(override val name: String) : AnimationType(name)

object Idle : PawnAnimationType("idle")
object Slash1H : PawnAnimationType("slash1H")
object WalkU : PawnAnimationType("walkU")
object WalkL : PawnAnimationType("walkL")