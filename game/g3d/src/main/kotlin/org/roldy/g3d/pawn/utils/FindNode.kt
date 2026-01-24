package org.roldy.g3d.pawn.utils

import com.badlogic.gdx.graphics.g3d.model.Node

fun Iterable<Node>.findNode(id: String): Node? =
    firstNotNullOfOrNull {
        if (it.id != id) {
            it.children.findNode(id)
        } else {
            it
        }
    }