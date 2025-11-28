package org.roldy.pawn.skeleton.attribute

abstract class PawnWeaponSlot(name: String) : PawnSkeletonSlot(name) {
    object MainHand : PawnWeaponSlot("weaponRight")
    object OffHand : PawnWeaponSlot("weaponLeft")
    object Shield : PawnWeaponSlot("shield")

    companion object Companion {
        val all by lazy {
            listOf(MainHand, OffHand, Shield)
        }
    }
}