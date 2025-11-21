package org.roldy.json

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class SpriteMetadata(
    val name: String,
    val metadata: MetaConfig
)

@Serializable
data class MetaConfig(
    val pivot: Vector
)

@Serializable
data class Vector(
    val x: Float,
    val y: Float
)

fun loadSpriteMetadata(fileHandle: FileHandle): List<SpriteMetadata> =
    Json.decodeFromString<List<SpriteMetadata>>(fileHandle.readString())