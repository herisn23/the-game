package org.roldy.terrain

import java.util.*
import kotlin.math.floor
import kotlin.math.sqrt

class SimplexNoise(seed: Long = System.currentTimeMillis()) {

    private val perm = IntArray(512)
    private val permMod12 = IntArray(512)

    private val grad3 = arrayOf(
        intArrayOf(1, 1, 0), intArrayOf(-1, 1, 0), intArrayOf(1, -1, 0), intArrayOf(-1, -1, 0),
        intArrayOf(1, 0, 1), intArrayOf(-1, 0, 1), intArrayOf(1, 0, -1), intArrayOf(-1, 0, -1),
        intArrayOf(0, 1, 1), intArrayOf(0, -1, 1), intArrayOf(0, 1, -1), intArrayOf(0, -1, -1)
    )

    init {
        val p = IntArray(256)
        val random = Random(seed)

        for (i in 0 until 256) {
            p[i] = i
        }

        // Shuffle
        for (i in 255 downTo 1) {
            val j = random.nextInt(i + 1)
            val temp = p[i]
            p[i] = p[j]
            p[j] = temp
        }

        for (i in 0 until 512) {
            perm[i] = p[i and 255]
            permMod12[i] = perm[i] % 12
        }
    }

    private fun dot(g: IntArray, x: Float, y: Float): Float {
        return g[0] * x + g[1] * y
    }

    private fun noiseInternal(xin: Float, yin: Float): Float {
        val F2:Float = 0.5f * (sqrt(3.0) - 1.0).toFloat()
        val G2:Float = ((3.0f - sqrt(3.0)) / 6.0f).toFloat()

        var n0: Float
        var n1: Float
        var n2: Float

        // Skew the input space to determine which simplex cell we're in
        val s = (xin + yin) * F2
        val i = floor(xin + s).toInt()
        val j = floor(yin + s).toInt()

        val t = (i + j) * G2
        val X0 = i - t
        val Y0 = j - t
        val x0 = xin - X0
        val y0 = yin - Y0

        // Determine which simplex we are in
        val i1: Int
        val j1: Int
        if (x0 > y0) {
            i1 = 1
            j1 = 0
        } else {
            i1 = 0
            j1 = 1
        }

        val x1 = x0 - i1 + G2.toFloat()
        val y1 = y0 - j1 + G2.toFloat()
        val x2 = x0 - 1.0f + 2.0f * G2.toFloat()
        val y2 = y0 - 1.0f + 2.0f * G2.toFloat()

        // Work out the hashed gradient indices of the three simplex corners
        val ii = i and 255
        val jj = j and 255
        val gi0 = permMod12[ii + perm[jj]]
        val gi1 = permMod12[ii + i1 + perm[jj + j1]]
        val gi2 = permMod12[ii + 1 + perm[jj + 1]]

        // Calculate the contribution from the three corners
        var t0 = 0.5f - x0 * x0 - y0 * y0
        if (t0 < 0) {
            n0 = 0.0f
        } else {
            t0 *= t0
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0)
        }

        var t1 = 0.5f - x1 * x1 - y1 * y1
        if (t1 < 0) {
            n1 = 0.0f
        } else {
            t1 *= t1
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1)
        }

        var t2 = 0.5f - x2 * x2 - y2 * y2
        if (t2 < 0) {
            n2 = 0.0f
        } else {
            t2 *= t2
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2)
        }

        // Add contributions from each corner to get the final noise value
        // The result is scaled to return values in the interval [-1,1]
        return 70.0f * (n0 + n1 + n2)
    }

    // Octave noise for more natural looking results
    operator fun invoke(x: Float, y: Float, octaves: Int = 4, persistence: Float = 0.5f, lacunarity: Float = 2.0f): Float {
        var total = 0f
        var frequency = 1f
        var amplitude = 1f
        var maxValue = 0f

        for (i in 0 until octaves) {
            total += noiseInternal(x * frequency, y * frequency) * amplitude
            maxValue += amplitude
            amplitude *= persistence
            frequency *= lacunarity
        }

        return total / maxValue
    }
}