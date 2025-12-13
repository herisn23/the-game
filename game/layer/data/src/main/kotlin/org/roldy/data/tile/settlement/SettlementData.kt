package org.roldy.data.tile.settlement

import com.badlogic.gdx.graphics.Color
import org.roldy.core.Vector2Int
import org.roldy.data.tile.mine.harvestable.Harvestable


data class SettlementData(
    val coords: Vector2Int,
    val name: String,
    val radius: List<Vector2Int>,
    val harvestable: List<Harvestable>,
    val color: Color
)