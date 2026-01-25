package org.roldy.g3d.pawn.part

import org.roldy.g3d.pawn.BodyType

data class BodyPart(
    val index: Int,
    val name: String,
    val part: Part.Type,
    val bodyType: BodyType,
    val noHair: Boolean = false,
    val noFacial: Boolean = false
)

fun main() {

}