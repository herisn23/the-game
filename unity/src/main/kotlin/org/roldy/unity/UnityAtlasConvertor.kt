package org.roldy.unity

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.yaml.snakeyaml.Yaml
import kotlin.system.exitProcess

val yaml = Yaml()
val objectMapper = jacksonObjectMapper()
const val sourceContext = "/Users/lukastreml/My project/"
const val spineContext = "spine"

fun main() {
    Lwjgl3Application(object : ApplicationAdapter() {
        override fun create() {
//            repackWeapons()
//            repackShields()
//            repackBodyParts()
//            repackTerrain()
            repackTerrainTextures()
            exitProcess(0)
        }
    })

}