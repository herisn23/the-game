package org.roldy.pawn.skeleton.attribute

abstract class PawnWeaponSlot(name: String) : PawnSkeletonSlot(name) {
    object WeaponRight : PawnWeaponSlot("weaponRight")
    object WeaponLeft : PawnWeaponSlot("weaponLeft")
    object Shield : PawnWeaponSlot("shield")

    companion object Companion {
        val all by lazy {
            listOf(WeaponRight, WeaponLeft, Shield)
        }
    }
}