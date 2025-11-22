package codegen

import java.util.*

/**
 * Utility for normalizing strings into valid Java/Kotlin class names
 * and avoiding duplicates.
 */
object ClassNameNormalizer {

    private val seenNames = mutableSetOf<String>()

    fun clean() =
        seenNames.clear()

    /**
     * Normalizes a string to a valid class name.
     *
     * Examples:
     * - "Kopeynik [ShowEars]" -> "KopeynikShowEars"
     * - "Kopeynik" -> "Kopeynik"
     * - "user-profile" -> "UserProfile"
     * - "123 Invalid" -> "Invalid123"
     * - "Hello World!" -> "HelloWorld"
     *
     * @param input The string to normalize
     * @return A valid class name
     */
    fun normalize(input: String): String {
        // Remove brackets and their contents, or just the brackets
        var normalized = input
            .replace(Regex("\\[.*?\\]"), "") // Remove [content]
            .replace(Regex("[\\[\\]()]"), "") // Remove remaining brackets/parens

        // Split by non-alphanumeric characters and capitalize each word
        normalized = normalized
            .split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.isNotEmpty() }
            .joinToString("") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
            }

        // If starts with a number, move numbers to end
        if (normalized.isNotEmpty() && normalized[0].isDigit()) {
            val digits = normalized.takeWhile { it.isDigit() }
            val rest = normalized.dropWhile { it.isDigit() }
            normalized = rest + digits
        }

        // If still empty or invalid, use default
        if (normalized.isEmpty() || !isValidClassName(normalized)) {
            normalized = "Generated"
        }

        return normalized
    }

    /**
     * Normalizes a string and ensures it's unique by adding a numeric suffix if needed.
     *
     * Examples:
     * - First call with "Kopeynik [ShowEars]" -> "KopeynikShowEars"
     * - Second call with "Kopeynik" -> "Kopeynik" (different after normalization)
     * - If "Kopeynik" already exists -> "Kopeynik2"
     *
     * @param input The string to normalize
     * @return A unique valid class name
     */
    fun normalizeUnique(input: String): String {
        val baseName = normalize(input)

        if (!seenNames.contains(baseName)) {
            seenNames.add(baseName)
            return baseName
        }

        // Find next available number suffix
        var counter = 2
        var uniqueName = "$baseName$counter"
        while (seenNames.contains(uniqueName)) {
            counter++
            uniqueName = "$baseName$counter"
        }

        seenNames.add(uniqueName)
        return uniqueName
    }

    /**
     * Normalizes multiple strings and ensures they're all unique.
     * Returns a map of original strings to normalized class names.
     *
     * @param inputs The strings to normalize
     * @return Map of original string to unique class name
     */
    fun normalizeAll(inputs: Collection<String>): Map<String, String> {
        reset()
        return inputs.associateWith { normalizeUnique(it) }
    }

    /**
     * Checks if two strings would normalize to the same class name.
     */
    fun wouldCollide(str1: String, str2: String): Boolean {
        return normalize(str1) == normalize(str2)
    }

    /**
     * Resets the seen names cache.
     * Useful when starting a new generation batch.
     */
    fun reset() {
        seenNames.clear()
    }

    /**
     * Checks if a string is a valid Java/Kotlin class name.
     */
    private fun isValidClassName(name: String): Boolean {
        if (name.isEmpty()) return false
        if (!name[0].isJavaIdentifierStart()) return false
        return name.all { it.isJavaIdentifierPart() }
    }

    /**
     * Gets all currently registered names (for debugging).
     */
    fun getSeenNames(): Set<String> = seenNames.toSet()
}