package org.roldy.pawn.skeleton.attribute

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
    fun regionName(orientation: PawnSkeletonOrientation): String =
        when (orientation) {
            is Left, Right -> "left"
            else -> orientation.value
        }

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
        val hairSlots by lazy {
            listOf(Hair, Beard)
        }
        val hideableSlotsByArmor: Map<ArmorPawnSlot.Piece, List<CustomizablePawnSkinSlot>> by lazy {
            mapOf(
                ArmorPawnSlot.Piece.Helmet to listOf(
                    Hair, Beard, Ears.EarLeft, Ears.EarRight
                )
            )
        }
    }
}

abstract class ArmorPawnSlot(override val name: String, protected val stripedRegionName: String) :
    PawnSkeletonSlot(name) {

    fun regionName(orientation: PawnSkeletonOrientation): String =
        when (orientation) {
            is Left, Right -> "left${stripedRegionName}"
            else -> "${orientation.value}${stripedRegionName}"
        }

    enum class Piece {
        Body, Helmet, Legs
    }

    object Helmet : ArmorPawnSlot("helmet", "Head")
    object ArmLeft : ArmorPawnSlot("armArmorLeft", "ArmLeft")
    object SleeveLeft : ArmorPawnSlot("armSleeveArmorLeft", "SleeveLeft")
    object HandLeft : ArmorPawnSlot("armHandArmorLeft", "HandLeft")
    object ArmRight : ArmorPawnSlot("armArmorRight", "ArmRight")
    object SleeveRight : ArmorPawnSlot("armSleeveArmorRight", "SleeveRight")
    object HandRight : ArmorPawnSlot("armHandArmorRight", "HandRight")
    object Body : ArmorPawnSlot("bodyArmor", "Body")
    object LegLeft : ArmorPawnSlot("legArmorLeft", "LegLeft")
    object LegRight : ArmorPawnSlot("legArmorRight", "LegRight")


    companion object {
        val allParts: List<ArmorPawnSlot> by lazy {
            listOf(
                Helmet, ArmRight, ArmLeft,
                SleeveRight, SleeveLeft,
                HandLeft, HandRight,
                Body, LegLeft, LegRight
            )
        }
        val pieces: Map<Piece, List<ArmorPawnSlot>> by lazy {
            mapOf(
                Piece.Body to listOf(Body, ArmLeft, ArmRight, SleeveLeft, SleeveRight, HandLeft, HandRight),
                Piece.Helmet to listOf(Helmet),
                Piece.Legs to listOf(LegLeft, LegRight)
            )
        }
        val slotPiece: Map<ArmorPawnSlot, Piece> by lazy {
            pieces.map { (piece, slots) ->
                slots.map { slot ->
                    slot to piece
                }
            }.flatten().toMap()
        }
    }
}