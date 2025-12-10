package org.roldy.rendering.environment

import com.badlogic.gdx.utils.Pool
import org.roldy.rendering.environment.item.SpriteTileObject

object BehaviourPool {
    private val spritePools = object : Pool<SpriteTileObject>() {
        override fun newObject(): SpriteTileObject = SpriteTileObject()
    }

    fun obtain(data: TileObject.Data): TileBehaviour {
        return when (data) {
            is SpriteTileObject.Data -> spritePools.obtain()
            else -> throw Exception("Unsupported data type to obtain behaviour ${data::class}")
        }
    }

    fun free(behaviour: TileBehaviour) {
        when (behaviour) {
            is SpriteTileObject -> spritePools.free(behaviour)
            else -> throw Exception("Unsupported behaviour to free ${behaviour::class}")
        }
    }
}