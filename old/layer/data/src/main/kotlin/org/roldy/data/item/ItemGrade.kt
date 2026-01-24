package org.roldy.data.item

import com.badlogic.gdx.graphics.Color

enum class ItemGrade(
    val color: Color
) {
    R(Color.RED),
    S(Color.ORANGE),
    A(Color.PURPLE),
    B(Color.BLUE),
    C(Color.GREEN),
    D(Color.GRAY)
}