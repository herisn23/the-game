package org.roldy.g3d.pawn.part

interface Body {
    val parts: Map<BodyPart, List<String>>
    val singleParts: List<BodyPart>
    val modularParts: List<BodyPart>
    operator fun get(part: BodyPart) = parts.getValue(part)
}

enum class BodyPart {
    Head, Hair, Beard, Body, Boots, Gauntlets, Legs
}