package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3

class EnvModelInstance(
    val name: String,
    val lod: Map<Int, ModelInstance>
) {

    companion object {
        const val LOD0_THRESHOLD = 50f
        const val LOD1_THRESHOLD = 100f
        const val LOD2_THRESHOLD = 200f
        const val LOD3_THRESHOLD = 300f
    }

    private val lodLevels = lod.keys.toList()
    private val maxLod = lodLevels.max()
    private val tmpPos = Vector3()
    private val hasLod = lod.size > 1
    private val firstInstance = lod.values.first()
    private val position get() = firstInstance.transform.getTranslation(tmpPos)

    fun setTranslation(ox: Float, oy: Float, oz: Float) {
        lod.forEach { (_, instance) ->
            instance.transform.idt()
            instance.transform.setTranslation(ox, oy, oz)
        }
    }

    private fun getLodLevel(dist: Float) =
        when {
            dist < LOD0_THRESHOLD -> 0
            dist < LOD1_THRESHOLD -> 1
            dist < LOD2_THRESHOLD -> 2
            dist < LOD3_THRESHOLD -> 3
            else -> -1
        }.clampLodLevel()

    private fun Int.clampLodLevel(): Int {
        if (this < 0) return maxLod
        if (this >= lodLevels.size) return maxLod
        return this
    }

    context(camera: Camera)
    private fun getLodInstance(): ModelInstance {
        val dist = camera.position.dst(position)
        val lodLevel = getLodLevel(dist)
        return lod.getValue(lodLevel)
    }


    context(camera: Camera)
    fun instance() =
        if (hasLod) {
            getLodInstance()
//            lod.getValue(1)
        } else {
            lod.getValue(-1)
        }

}

