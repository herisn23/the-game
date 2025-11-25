package org.roldy.pawn.skeleton.attribute

import org.roldy.animation.AnimationType

abstract class PawnAnimationType(override val name: String) : AnimationType(name)

object Idle : PawnAnimationType("idle")
object Slash1H : PawnAnimationType("slash1H")