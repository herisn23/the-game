package org.roldy.pawn.skeleton.attribute

abstract class UnderWearSlot(name: String, val part: String) : SkinPawnSkeletonSlot("$name$part") {
    fun regionName(orientation: PawnSkeletonOrientation) =
        "${orientation.name}$part"

    object Body : UnderWearSlot("underwear", "Body")
    object LegLeft : UnderWearSlot("underwear", "LegLeft")
    object LegRight : UnderWearSlot("underwear", "LegRight")

    companion object {
        val allParts by lazy {
            listOf(Body, LegLeft, LegRight)
        }
    }
}