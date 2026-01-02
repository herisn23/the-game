package codegen



fun getRegionNames(lines:List<String>): Set<String> {
    val regionNames = mutableListOf<String>()

    for (line in lines) {
        // Region names are lines that don't start with whitespace
        // and aren't empty or texture page declarations
        if (line.isNotBlank() &&
            !line.startsWith(" ") &&
            !line.startsWith("\t") &&
            !line.contains(".png") &&
            !line.contains(".jpg")) {

            // Check if it's not a property line (size:, format:, etc.)
            if (!line.contains(":")) {
                regionNames.add(line.trim())
            }
        }
    }

    return regionNames.toSet()
}