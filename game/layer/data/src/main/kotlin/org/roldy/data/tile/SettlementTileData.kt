package org.roldy.data.tile

import com.badlogic.gdx.graphics.Color
import org.roldy.core.Vector2Int

data class SettlementTileData(
    val id: Int,
    override val coords: Vector2Int,
    val name: String,
    val claims: List<Vector2Int>,
    val harvestableCount: Int,
    val type: Int,
    val color: Color
): TileData