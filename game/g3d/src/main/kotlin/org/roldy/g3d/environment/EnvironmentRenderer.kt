package org.roldy.g3d.environment

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.foliageShaderProvider
import org.roldy.core.shader.shiftingShaderProvider
import org.roldy.core.system.ShadowSystem
import org.roldy.core.system.WindSystem

class EnvironmentRenderer(
    shaderProvider: ShaderProvider,
    private val offsetProvider: OffsetProvider
) : AutoDisposableAdapter() {

    private val batch by disposable { ModelBatch(shaderProvider) }
    var currentRenderedModels = 0
    context(camera: Camera, environment: Environment)
    fun render(instances: List<EnvModelInstance>) {
        batch.begin(camera)
        currentRenderedModels = 0
        instances.forEach {
            val model = it.get()
            if (isVisible(it, model)) {
                currentRenderedModels += 1
                batch.render(model.instance, environment)
            }
        }
        batch.end()
        Gdx.gl.glDepthMask(true)
        Gdx.gl.glDepthFunc(GL20.GL_LESS)
    }

    context(camera: Camera)
    fun ShadowSystem.renderShadows(instances: List<EnvModelInstance>) {
        instances.forEach {
            val model = it.get()
            if (isVisible(it, model))
                render(model.instance)
        }
    }

    private val tmpBox = BoundingBox()

    context(camera: Camera)
    fun isVisible(instance: EnvModelInstance, model: EnvModelInstance.ModelInstanceWrapper): Boolean {
        val offset = offsetProvider.shiftOffset
        // Copy chunk bounds and apply offset
        tmpBox.set(model.boundingBox)
        // First: move to world position
        tmpBox.min.add(instance.position)
        tmpBox.max.add(instance.position)

        // Then: apply shift offset
        tmpBox.min.sub(offset)
        tmpBox.max.sub(offset)
        return camera.frustum.boundsInFrustum(tmpBox)
    }

}

fun foliageModelRenderer(
    windSystem: WindSystem,
    offsetProvider: OffsetProvider,
) =
    EnvironmentRenderer(
        foliageShaderProvider(offsetProvider, windSystem),
        offsetProvider
    )

fun staticModelRenderer(
    offsetProvider: OffsetProvider,
) =
    EnvironmentRenderer(shiftingShaderProvider(offsetProvider), offsetProvider)