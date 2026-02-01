package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3

class SunSphere(
    private val light: DirectionalLight,
    private val distance: Float = 80000f,
    private val radius: Float = 20000f
) {
    val modelInstance: ModelInstance
    private val model: Model
    private val position = Vector3()

    init {
        val modelBuilder = ModelBuilder()

        // Create emissive material (unlit, glowing)
        val material = Material(
            ColorAttribute.createDiffuse(light.color),
            ColorAttribute.createEmissive(light.color)
        )

        // Create sphere
        model = modelBuilder.createSphere(
            radius * 2, radius * 2, radius * 2,
            32, 32,
            material,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )

        modelInstance = ModelInstance(model)
        updatePosition()
    }

    fun updatePosition() {
        // Place sun in opposite direction of light
        position.set(light.direction).nor().scl(-distance)
        modelInstance.transform.setToTranslation(position)
    }

    fun updateColor() {
        val material = modelInstance.materials[0]
        material.set(ColorAttribute.createDiffuse(light.color))
        material.set(ColorAttribute.createEmissive(light.color))
    }

    fun dispose() {
        model.dispose()
    }
}