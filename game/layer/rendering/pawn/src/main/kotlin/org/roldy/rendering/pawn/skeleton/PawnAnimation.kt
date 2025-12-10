package org.roldy.rendering.pawn.skeleton

interface PawnAnimation {
    fun idle()
    fun slash1H(speed: Float)
    fun walk(speed: Float)
    fun stop()
}
