package org.roldy.g3d.pawn.part

interface Armor {
    val armors: Map<ArmorPart, List<String>>
    val sets: Map<String, Map<ArmorPart, String>>
}

enum class ArmorPart {
    Body, Boots, Gauntlets, Cape, Helmet, Legs
}

val armorToBody = mapOf(
    ArmorPart.Body to listOf(BodyPart.Body),
    ArmorPart.Boots to listOf(BodyPart.Boots),
    ArmorPart.Gauntlets to listOf(BodyPart.Gauntlets),
    ArmorPart.Legs to listOf(BodyPart.Legs),
    ArmorPart.Helmet to listOf(BodyPart.Head, BodyPart.Hair, BodyPart.Beard),
    ArmorPart.Cape to emptyList()
)