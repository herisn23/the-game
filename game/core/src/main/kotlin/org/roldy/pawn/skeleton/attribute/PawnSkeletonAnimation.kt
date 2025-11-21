package org.roldy.pawn.skeleton.attribute

import org.roldy.pawn.NamedAttribute

abstract class PawnAnimation(override val name: String) : NamedAttribute()

object Idle : PawnAnimation("idle")