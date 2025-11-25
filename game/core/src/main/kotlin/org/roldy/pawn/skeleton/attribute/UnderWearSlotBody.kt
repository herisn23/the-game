package org.roldy.pawn.skeleton.attribute

abstract class UnderWearSlotBody(name: String, val part: String) : PawnBodySkeletonSlot("$name$part") {
    fun regionName(orientation: PawnSkeletonOrientation) =
        "${orientation.name}$part"

    object Body : UnderWearSlotBody("underwear", "Body")
    object LegLeft : UnderWearSlotBody("underwear", "LegLeft")
    object LegRight : UnderWearSlotBody("underwear", "LegRight")

    companion object {
        val allParts by lazy {
            listOf(Body, LegLeft, LegRight)
        }
    }
}