package org.roldy.pawn.skeleton.attribute


abstract class PawnBodySkeletonSlot(override val name: String) : PawnSkeletonSlot(name) {
    object Head : PawnBodySkeletonSlot("head")
    object Body : PawnBodySkeletonSlot("body")
    object ArmLeft : PawnBodySkeletonSlot("armLeft")
    object ArmRight : PawnBodySkeletonSlot("armRight")
    object HandLeft : PawnBodySkeletonSlot("handLeft")
    object HandRight : PawnBodySkeletonSlot("handRight")
    object LegLeft : PawnBodySkeletonSlot("legLeft")
    object LegRight : PawnBodySkeletonSlot("legRight")
    object EarLeft : PawnBodySkeletonSlot("earLeft")
    object EarRight : PawnBodySkeletonSlot("earRight")
    object FingersLeft : PawnBodySkeletonSlot("fingersLeft")
    object FingersRight : PawnBodySkeletonSlot("fingersRight")
    companion object Companion {
        val allParts: List<PawnBodySkeletonSlot> =
            listOf(
                Head, Body, ArmLeft, ArmRight, HandLeft,
                HandRight, LegLeft, LegRight, EarLeft, EarRight,
                FingersLeft, FingersRight
            )
    }
}