package org.roldy.g3d.environment

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.coroutines.ConcurrentLoop
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.foliageShaderProvider
import org.roldy.core.shader.shiftingShaderProvider
import org.roldy.core.system.ShadowSystem
import org.roldy.core.system.WindSystem

class EnvironmentRenderer(
    private val camera: Camera,
    shaderProvider: ShaderProvider,
    private val offsetProvider: OffsetProvider,
    private val instancesProvider: () -> List<EnvModelInstance>
) : AutoDisposableAdapter() {

    private val eventTask = ConcurrentLoop(emitter = ::process)

    private val batch by disposable { ModelBatch(shaderProvider) }

    private val instancesCache = mutableListOf<EnvModelInstance.ModelInstanceWrapper>()

    val currentRenderedModels get() = instancesCache.size

    init {
        eventTask.start()
    }

    val visibleInstances: List<EnvModelInstance.ModelInstanceWrapper>
        get() = synchronized(instancesCache) { instancesCache.toList() }

    context(environment: Environment)
    fun render() {
        batch.begin(camera)
        visibleInstances.forEach {
            batch.render(it.instance, environment)
        }
        batch.end()
        Gdx.gl.glDepthMask(true)
        Gdx.gl.glDepthFunc(GL20.GL_LESS)
    }

    fun ShadowSystem.renderShadows() {
        visibleInstances.forEach {
            render(it.instance)
        }
    }

    private val tmpBox = BoundingBox()

    context(camera: Camera)
    private fun isVisible(instance: EnvModelInstance, model: EnvModelInstance.ModelInstanceWrapper): Boolean {
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

    private fun process() {
        synchronized(instancesCache) {
            instancesCache.clear()
            val instances = instancesProvider()
            context(camera) {
                instances.forEach {
                    val model = it.get()
                    if (isVisible(it, model)) {
                        instancesCache.add(model)
                    }
                }
            }
        }
    }

}

fun foliageModelRenderer(
    camera: Camera,
    windSystem: WindSystem,
    offsetProvider: OffsetProvider,
    instancesProvider: () -> List<EnvModelInstance>
) =
    EnvironmentRenderer(
        camera,
        foliageShaderProvider(offsetProvider, windSystem),
        offsetProvider,
        instancesProvider
    )

fun staticModelRenderer(
    camera: Camera,
    offsetProvider: OffsetProvider,
    instancesProvider: () -> List<EnvModelInstance>
) =
    EnvironmentRenderer(camera, shiftingShaderProvider(offsetProvider), offsetProvider, instancesProvider)