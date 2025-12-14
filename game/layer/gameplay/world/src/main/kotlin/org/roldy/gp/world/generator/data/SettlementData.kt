package org.roldy.gp.world.generator.data

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import org.roldy.core.Vector2Int

data class SettlementData(
    val coords: Vector2Int,
    val name: String,
    val radius: Int,
    val radiusCoords: List<Vector2Int>,
    val harvestableCount: Int,
    val color: Color,
    val texture: String,
)