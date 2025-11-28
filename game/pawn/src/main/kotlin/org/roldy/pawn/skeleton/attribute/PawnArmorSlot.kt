package org.roldy.pawn.skeleton.attribute

abstract class PawnArmorSlot(override val name: String, protected val stripedRegionName: String) :
    PawnSkeletonSlot(name) {

    fun regionName(orientation: PawnSkeletonOrientation): String =
        "${orientation.capitalizedName}${stripedRegionName}"

    enum class Piece {
        Body, Helmet, Legs, Gloves
    }

    object Helmet : PawnArmorSlot("helmet", "Head")
    object ArmLeft : PawnArmorSlot("armArmorLeft", "ArmLeft")
    object SleeveLeft : PawnArmorSlot("sleeveArmorLeft", "SleeveLeft")
    object HandLeft : PawnArmorSlot("handArmorLeft", "HandLeft")
    object ArmRight : PawnArmorSlot("armArmorRight", "ArmRight")
    object SleeveRight : PawnArmorSlot("sleeveArmorRight", "SleeveRight")
    object HandRight : PawnArmorSlot("handArmorRight", "HandRight")
    object Body : PawnArmorSlot("bodyArmor", "Body")
    object LegLeft : PawnArmorSlot("legArmorLeft", "LegLeft")
    object LegRight : PawnArmorSlot("legArmorRight", "LegRight")
    object FingersLeft : PawnArmorSlot("fingersArmorLeft", "FingersLeft")
    object FingersRight : PawnArmorSlot("fingersArmorRight", "FingersRight")


    companion object Companion {
        val allParts: List<PawnArmorSlot> by lazy {
            pieces.values.flatten()
        }
        val pieces: Map<Piece, List<PawnArmorSlot>> by lazy {
            mapOf(
                Piece.Body to listOf(Body, ArmLeft, ArmRight),
                Piece.Gloves to listOf(SleeveLeft, SleeveRight, HandLeft, HandRight, FingersLeft, FingersRight),
                Piece.Helmet to listOf(Helmet),
                Piece.Legs to listOf(LegLeft, LegRight)
            )
        }
        val slotPiece: Map<PawnArmorSlot, Piece> by lazy {
            pieces.map { (piece, slots) ->
                slots.map { slot ->
                    slot to piece
                }
            }.flatten().toMap()
        }
    }
}