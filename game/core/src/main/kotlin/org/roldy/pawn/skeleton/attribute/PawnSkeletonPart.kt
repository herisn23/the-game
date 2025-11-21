package org.roldy.pawn.skeleton.attribute

import org.roldy.pawn.NamedAttribute

abstract class PawnSkeletonPart(override val name: String) : NamedAttribute()

abstract class SkinPawnSkeletonPart(override val name: String) : PawnSkeletonPart(name) {
    companion object {
        object Head : SkinPawnSkeletonPart("head")
        object Body : SkinPawnSkeletonPart("body")
        object ArmLeft : SkinPawnSkeletonPart("armLeft")
        object ArmRight : SkinPawnSkeletonPart("armRight")
        object HandLeft : SkinPawnSkeletonPart("handLeft")
        object HandRight : SkinPawnSkeletonPart("handRight")
        object LegLeft : SkinPawnSkeletonPart("legLeft")
        object LegRight : SkinPawnSkeletonPart("legRight")
        object EarLeft : SkinPawnSkeletonPart("earLeft")
        object EarRight : SkinPawnSkeletonPart("earRight")

        val allParts: List<SkinPawnSkeletonPart> =
            listOf(Head, Body, ArmLeft, ArmRight, HandLeft, HandRight, LegLeft, LegRight, EarLeft, EarRight)
    }
}

abstract class CustomizablePawnSkinPart(override val name: String) : SkinPawnSkeletonPart(name) {
    companion object {
        object Head : CustomizablePawnSkinPart("head")
        object Eyes : CustomizablePawnSkinPart("eyes")
        object Eyebrows : CustomizablePawnSkinPart("eyebrows")
        object Beard : CustomizablePawnSkinPart("beard")
        object Mouth : CustomizablePawnSkinPart("mouth")
        object Ears {
            object EarLeft : CustomizablePawnSkinPart("earLeft")
            object EarRight : CustomizablePawnSkinPart("earRight")
        }

        val allParts: List<CustomizablePawnSkinPart> = listOf(
            Head, Eyes, Eyebrows, Beard, Mouth, Ears.EarLeft, Ears.EarRight
        )
    }
}