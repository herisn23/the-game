package org.roldy.core

interface HeightSampler {
    fun isInBounds(localX: Float, localZ: Float): Boolean
    fun getHeightAt(localX: Float, localZ: Float): Float
}