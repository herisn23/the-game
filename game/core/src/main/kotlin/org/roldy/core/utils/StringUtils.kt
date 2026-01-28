package org.roldy.core.utils


infix fun String.cycle(count: Int) =
    (0..count).joinToString("") { this }

enum class NamingStyle {
    SNAKE_CASE,           // hello_world
    SCREAMING_SNAKE_CASE, // HELLO_WORLD
    CAMEL_CASE,           // helloWorld
    PASCAL_CASE,          // HelloWorld
    KEBAB_CASE,           // hello-world
    SCREAMING_KEBAB_CASE, // HELLO-WORLD
    TRAIN_CASE,           // Hello-World
    LOWER_CASE,           // helloworld
    UPPER_CASE            // HELLOWORLD
}

fun String.convertCase(from: NamingStyle, to: NamingStyle): String {
    // Step 1: Parse into words based on source style
    val words = parseWords(this, from)

    // Step 2: Format words according to target style
    return formatWords(words, to)
}

private fun parseWords(text: String, style: NamingStyle): List<String> {
    return when (style) {
        NamingStyle.SNAKE_CASE,
        NamingStyle.SCREAMING_SNAKE_CASE ->
            text.split('_').filter { it.isNotEmpty() }

        NamingStyle.KEBAB_CASE,
        NamingStyle.SCREAMING_KEBAB_CASE,
        NamingStyle.TRAIN_CASE ->
            text.split('-').filter { it.isNotEmpty() }

        NamingStyle.CAMEL_CASE,
        NamingStyle.PASCAL_CASE ->
            text.split("(?<=[a-z])(?=[A-Z])".toRegex()).filter { it.isNotEmpty() }

        NamingStyle.LOWER_CASE,
        NamingStyle.UPPER_CASE ->
            listOf(text) // No clear word boundaries
    }
}

private fun formatWords(words: List<String>, style: NamingStyle): String {
    return when (style) {
        NamingStyle.SNAKE_CASE ->
            words.joinToString("_") { it.lowercase() }

        NamingStyle.SCREAMING_SNAKE_CASE ->
            words.joinToString("_") { it.uppercase() }

        NamingStyle.CAMEL_CASE ->
            words.mapIndexed { index, word ->
                if (index == 0) word.lowercase()
                else word.lowercase().replaceFirstChar { it.uppercase() }
            }.joinToString("")

        NamingStyle.PASCAL_CASE ->
            words.joinToString("") {
                it.lowercase().replaceFirstChar { c -> c.uppercase() }
            }

        NamingStyle.KEBAB_CASE ->
            words.joinToString("-") { it.lowercase() }

        NamingStyle.SCREAMING_KEBAB_CASE ->
            words.joinToString("-") { it.uppercase() }

        NamingStyle.TRAIN_CASE ->
            words.joinToString("-") {
                it.lowercase().replaceFirstChar { c -> c.uppercase() }
            }

        NamingStyle.LOWER_CASE ->
            words.joinToString("") { it.lowercase() }

        NamingStyle.UPPER_CASE ->
            words.joinToString("") { it.uppercase() }
    }
}