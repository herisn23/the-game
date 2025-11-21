package org.roldy.pawn.skeleton.attribute

import org.roldy.pawn.NamedAttribute
import java.util.Locale

abstract class PawnSkeletonPart(override val name: String): NamedAttribute()


object Head: PawnSkeletonPart("head")
object Body: PawnSkeletonPart("body")
object ArmLeft: PawnSkeletonPart("armLeft")
object ArmRight: PawnSkeletonPart("armRight")
object HandLeft: PawnSkeletonPart("handLeft")
object HandRight: PawnSkeletonPart("handRight")
object LegLeft: PawnSkeletonPart("legLeft")
object LegRight: PawnSkeletonPart("legRight")