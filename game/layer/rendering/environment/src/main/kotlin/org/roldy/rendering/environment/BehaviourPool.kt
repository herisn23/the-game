package org.roldy.rendering.environment

import com.badlogic.gdx.utils.Pool
import org.roldy.rendering.environment.item.HarvestableTileBehaviour
import org.roldy.rendering.environment.item.SettlementTileBehaviour
import org.roldy.rendering.environment.item.SpriteTileBehaviour

object BehaviourPool {
    private val spritePools = object : Pool<SpriteTileBehaviour>() {
        override fun newObject(): SpriteTileBehaviour = SpriteTileBehaviour()
    }
    private val settlementPool = object : Pool<SettlementTileBehaviour>() {
        override fun newObject(): SettlementTileBehaviour = SettlementTileBehaviour()
    }
    private val harvestablePool = object : Pool<HarvestableTileBehaviour>() {
        override fun newObject(): HarvestableTileBehaviour = HarvestableTileBehaviour()
    }

    fun obtain(data: TileObject.Data): TileBehaviour {
        return when (data) {
            is SettlementTileBehaviour.Data -> settlementPool.obtain()
            is SpriteTileBehaviour.Data -> spritePools.obtain()
            is HarvestableTileBehaviour.Data -> harvestablePool.obtain()
            else -> throw Exception("Unsupported data type to obtain behaviour ${data::class}")
        }
    }

    fun free(behaviour: TileBehaviour) {
        when (behaviour) {
            is SettlementTileBehaviour -> settlementPool.free(behaviour)
            is SpriteTileBehaviour -> spritePools.free(behaviour)
            is HarvestableTileBehaviour -> harvestablePool.free(behaviour)
            else -> throw Exception("Unsupported behaviour to free ${behaviour::class}")
        }
    }
}