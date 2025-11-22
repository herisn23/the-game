package org.roldy.pawn.skeleton.attribute

abstract class WeaponPawnSlot(name: String) :PawnSkeletonSlot(name) {
    object WeaponRight: WeaponPawnSlot("weaponRight")

    companion object {
        val allParts by lazy {
            listOf(WeaponRight)
        }
    }
}