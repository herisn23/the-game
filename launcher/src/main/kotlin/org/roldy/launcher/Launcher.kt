package org.roldy.launcher

import org.roldy.Application


/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
//    if (StartupHelper.startNewJvmIfRequired()) return
    Application.start()
}