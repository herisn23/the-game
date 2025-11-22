package org.roldy.pawn.skeleton.attribute

abstract class ArmorPawnSlot(override val name: String, protected val stripedRegionName: String) :
    PawnSkeletonSlot(name) {

    fun regionName(orientation: PawnSkeletonOrientation): String =
        when (orientation) {
            is Left, Right -> "left${stripedRegionName}"
            else -> "${orientation.capitalizedName}${stripedRegionName}"
        }

    enum class Piece {
        Body, Helmet, Legs, Gloves
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
                Piece.Body to listOf(Body, ArmLeft, ArmRight),
                Piece.Gloves to listOf(SleeveLeft, SleeveRight, HandLeft, HandRight),
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