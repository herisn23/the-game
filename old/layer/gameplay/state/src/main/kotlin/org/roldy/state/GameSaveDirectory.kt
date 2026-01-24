package org.roldy.state

import java.io.File


fun getSaveDirectory(gameName: String): File {
    val os = System.getProperty("os.name").lowercase()

    val saveDir = when {
        os.contains("win") -> {
            // Windows: %APPDATA%/YourGame
            File(System.getenv("APPDATA"), gameName)
        }
        os.contains("mac") -> {
            // macOS: ~/Library/Application Support/YourGame
            File(System.getProperty("user.home"), "Library/Application Support/$gameName")
        }
        else -> {
            // Linux: ~/.local/share/YourGame
            File(System.getProperty("user.home"), ".local/share/$gameName")
        }
    }

    saveDir.mkdirs()
    return saveDir
}