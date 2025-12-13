package org.roldy.core

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


fun String.compress(): ByteArray {
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).use { it.write(this.toByteArray()) }
    return bos.toByteArray()
}

fun ByteArray.decompress(): String {
    return GZIPInputStream(this.inputStream()).use {
        it.readBytes().toString(Charsets.UTF_8)
    }
}