package org.roldy.pawn.skeleton.attribute

abstract class PawnAnimation(override val name: String) : NamedAttribute()

object Idle : PawnAnimation("idle")
object Slash1H : PawnAnimation("slash1H")