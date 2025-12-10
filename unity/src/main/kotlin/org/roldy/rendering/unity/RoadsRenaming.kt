package org.roldy.rendering.unity

import java.nio.file.Path


fun main() {
    val names = Path.of("resources/hex_environments/roads").toFile().listFiles().filter {
        !it.name.contains("Bridge")
    }.map { it.nameWithoutExtension.replace("hexRoad-", "").split("-") }

    val map = names.sortedBy { it[0] }.associate {
        it[0] to it[1]
    }
    println(map)
}