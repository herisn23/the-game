package org.roldy.pawn.skeleton.attribute

abstract class PawnSkeletonOrientation(override val name: String): NamedAttribute()

object Front : PawnSkeletonOrientation("front")
object Back : PawnSkeletonOrientation("back")
object Left : PawnSkeletonOrientation("left")
object Right : PawnSkeletonOrientation("right")