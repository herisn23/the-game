package org.roldy.g3d.pawn.part

interface Armor {
    val armors: Map<ArmorPart, List<String>>
    val sets: Map<String, List<Pair<ArmorPart, String>>>
}

enum class ArmorPart {
    Body, Boots, Gauntlets, Cape, Helmet, Legs
}