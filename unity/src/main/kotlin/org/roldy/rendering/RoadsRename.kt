package org.roldy.rendering

import java.io.File

val folders = listOf(
    "dirt1",
    "dirt2",
    "dirt3",
    "dirt4",
    "snow",
    "stone1",
    "stone2",
    "stone3",
    "stone4"
)
val bitMaskMapping = listOf(
    1 to "100000",
    2 to "010000",
    3 to "000001",
    4 to "001000",
    5 to "000010",
    6 to "000100",
    7 to "100100",
    8 to "010010",
    9 to "001001",
    10 to "011000",
    11 to "110000",
    12 to "100001",
    13 to "000011",
    14 to "000110",
    15 to "001100",
    16 to "001010",
    17 to "101000",
    18 to "000101",
    19 to "010001",
    20 to "010100",
    21 to "100010",
    22 to "101001",
    23 to "011001",
    24 to "001011",
    25 to "001101",
    26 to "110100",
    27 to "100101",
    28 to "101100",
    29 to "100110",
    30 to "010011",
    31 to "011010",
    32 to "010110",
    33 to "110010",
    34 to "101010",
    35 to "010101",
    36 to "001110",
    37 to "000111",
    38 to "100011",
    39 to "110001",
    40 to "111000",
    41 to "011100",
    42 to "101110",
    43 to "111001",
    44 to "001111",
    45 to "011110",
    46 to "100111",
    47 to "010111",
    48 to "111100",
    49 to "110011",
    50 to "101011",
    51 to "110101",
    52 to "111010",
    53 to "011101",
    54 to "101101",
    55 to "011011",
    57 to "101111",
    58 to "011111",
    59 to "111011",
    60 to "111101",
    61 to "111110",
    62 to "110111",
    63 to "111111",
    64 to "110110"
)

fun main() {
    val target = File("resources/hex/roads/bitmask")
    fun naturalOrder(s: String): List<Any> {
        return Regex("(\\d+|\\D+)").findAll(s)
            .map { it.value.toIntOrNull() ?: it.value }
            .toList()
    }
    folders.forEach { folderName ->
        val sorted = File("resources/hex/roads/$folderName").listFiles()
            .sortedWith(compareBy({
                val index = it.nameWithoutExtension.roadIndex()
                index.split("_")[0].toInt()
            }, {
                val index = it.nameWithoutExtension.roadIndex()
                val splited = index.split("_")
                when (splited.size) {
                    1 -> 0
                    else -> -splited[1].toInt()
                }
            }))
        sorted.forEachIndexed { index, file ->
            val newName = "${folderName}_${bitMaskMapping[index].second}.png"
            file.copyTo(File(target, newName), true)
        }
    }
}

fun String.roadIndex() =
    substring(nthIndexOf('_', 3) + 1)

fun String.nthIndexOf(char: Char, n: Int): Int {
    var count = 0
    var index = -1

    repeat(n) {
        index = indexOf(char, index + 1)
        if (index == -1) return -1
    }

    return index
}