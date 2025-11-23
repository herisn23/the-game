package org.roldy.pawn.skeleton.attribute

abstract class ArmorPawnSlot(override val name: String, protected val stripedRegionName: String) :
    PawnSkeletonSlot(name) {

    fun regionName(orientation: PawnSkeletonOrientation): String =
        "${orientation.capitalizedName}${stripedRegionName}"

    enum class Piece {
        Body, Helmet, Legs, Gloves
    }

    object Helmet : ArmorPawnSlot("helmet", "Head")
    object ArmLeft : ArmorPawnSlot("armArmorLeft", "ArmLeft")
    object SleeveLeft : ArmorPawnSlot("sleeveArmorLeft", "SleeveLeft")
    object HandLeft : ArmorPawnSlot("handArmorLeft", "HandLeft")
    object ArmRight : ArmorPawnSlot("armArmorRight", "ArmRight")
    object SleeveRight : ArmorPawnSlot("sleeveArmorRight", "SleeveRight")
    object HandRight : ArmorPawnSlot("handArmorRight", "HandRight")
    object Body : ArmorPawnSlot("bodyArmor", "Body")
    object LegLeft : ArmorPawnSlot("legArmorLeft", "LegLeft")
    object LegRight : ArmorPawnSlot("legArmorRight", "LegRight")
    object FingersLeft : ArmorPawnSlot("fingersArmorLeft", "FingersLeft")
    object FingersRight : ArmorPawnSlot("fingersArmorRight", "FingersRight")


    companion object {
        val allParts: List<ArmorPawnSlot> by lazy {
            pieces.values.flatten()
        }
        val pieces: Map<Piece, List<ArmorPawnSlot>> by lazy {
            mapOf(
                Piece.Body to listOf(Body, ArmLeft, ArmRight),
                Piece.Gloves to listOf(SleeveLeft, SleeveRight, HandLeft, HandRight,FingersLeft, FingersRight),
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