package org.roldy.pawn.skeleton.attribute


abstract class SkinPawnSkeletonSlot(override val name: String) : PawnSkeletonSlot(name) {
    object Head : SkinPawnSkeletonSlot("head")
    object Body : SkinPawnSkeletonSlot("body")
    object ArmLeft : SkinPawnSkeletonSlot("armLeft")
    object ArmRight : SkinPawnSkeletonSlot("armRight")
    object HandLeft : SkinPawnSkeletonSlot("handLeft")
    object HandRight : SkinPawnSkeletonSlot("handRight")
    object LegLeft : SkinPawnSkeletonSlot("legLeft")
    object LegRight : SkinPawnSkeletonSlot("legRight")
    object EarLeft : SkinPawnSkeletonSlot("earLeft")
    object EarRight : SkinPawnSkeletonSlot("earRight")
    companion object {
        val allParts: List<SkinPawnSkeletonSlot> =
            listOf(Head, Body, ArmLeft, ArmRight, HandLeft, HandRight, LegLeft, LegRight, EarLeft, EarRight)
    }
}