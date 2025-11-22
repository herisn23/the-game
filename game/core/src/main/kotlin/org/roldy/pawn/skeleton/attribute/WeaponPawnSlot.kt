package org.roldy.pawn.skeleton.attribute

abstract class WeaponPawnSlot(name: String) : PawnSkeletonSlot(name) {
    object WeaponRight : WeaponPawnSlot("weaponRight")
    object WeaponLeft : WeaponPawnSlot("weaponLeft")

    companion object {
        val all by lazy {
            listOf(WeaponRight, WeaponLeft)
        }
    }
}