package org.roldy.pawn.skeleton.attribute

import org.roldy.pawn.NamedAttribute

abstract class PawnSkeletonSlot(override val name: String) : NamedAttribute()

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

abstract class CustomizablePawnSkinSlot(override val name: String) : SkinPawnSkeletonSlot(name) {
    object Head : CustomizablePawnSkinSlot("head")
    object Eyes : CustomizablePawnSkinSlot("eyes")
    object Hair : CustomizablePawnSkinSlot("hair")
    object EyesBrows : CustomizablePawnSkinSlot("eyesbrows")
    object Beard : CustomizablePawnSkinSlot("beard")
    object Mouth : CustomizablePawnSkinSlot("mouth")
    object Ears {
        object EarLeft : CustomizablePawnSkinSlot("earLeft")
        object EarRight : CustomizablePawnSkinSlot("earRight")
    }
    companion object {
        val allParts: List<CustomizablePawnSkinSlot> by lazy {
            listOf(
                Head, Hair, Eyes, EyesBrows, Beard, Mouth, Ears.EarLeft, Ears.EarRight
            )
        }
    }
}

abstract class ArmorPawnSlot(override val name: String) : PawnSkeletonSlot(name) {

    enum class Piece {
        Body, Helmet, Legs
    }
    object Helmet : ArmorPawnSlot("helmet")
    object ArmLeft : ArmorPawnSlot("armArmorLeft")
    object SleeveLeft : ArmorPawnSlot("armSleeveArmorLeft")
    object HandLeft : ArmorPawnSlot("armHandArmorLeft")
    object ArmRight : ArmorPawnSlot("armArmorRight")
    object SleeveRight : ArmorPawnSlot("armSleeveArmorRight")
    object HandRight : ArmorPawnSlot("armHandArmorRight")
    object Body : ArmorPawnSlot("bodyArmor")
    object LegLeft : ArmorPawnSlot("legArmorLeft")
    object LegRight : ArmorPawnSlot("legArmorRight")


    companion object {
        val allParts: List<ArmorPawnSlot> = listOf(
            Helmet, ArmRight, ArmLeft,
            SleeveRight, SleeveLeft,
            HandLeft, HandRight,
            Body, LegLeft, LegRight
        )
        val pieces: Map<Piece, List<ArmorPawnSlot>> = mapOf(
            Piece.Body to listOf(Body, ArmLeft, ArmRight, SleeveLeft, SleeveRight, HandLeft, HandRight),
            Piece.Helmet to listOf(Helmet),
            Piece.Legs to listOf(LegLeft, LegRight)
        )
    }
}