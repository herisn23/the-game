package org.roldy.pawn.skeleton.attribute

import org.roldy.core.NamedAttribute

abstract class PawnSkeletonOrientation(
    /**
     * This name is used to find region in atlas map.
     * Atlas contains only front, Back and Left. So left is used for right oriented skeleton.
     */
    override val name: String,
    val skeletonName:String = name,
    val scaleX:Float = 1f,
    val scaleY:Float = 1f,
): NamedAttribute() {

    companion object {
       val all by lazy {
           listOf(Front, Back, Left, Right)
       }
   }
}

object Front : PawnSkeletonOrientation("front")

object Back : PawnSkeletonOrientation("back")
object Left : PawnSkeletonOrientation("left")
object Right : PawnSkeletonOrientation("left", "right")