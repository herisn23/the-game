package org.roldy.pawn.skeleton.attribute

import org.roldy.pawn.NamedAttribute

abstract class PawnSkeletonOrientation(override val name: String): NamedAttribute()

object Front : PawnSkeletonOrientation("front")
object Back : PawnSkeletonOrientation("back")
object Left : PawnSkeletonOrientation("left")
object Right : PawnSkeletonOrientation("right")