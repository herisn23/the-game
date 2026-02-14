package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.foliageShaderProvider
import org.roldy.core.shader.shiftingShaderProvider
import org.roldy.core.system.WindSystem

class EnvironmentRenderer(
    shaderProvider: ShaderProvider
) : AutoDisposableAdapter() {

    private val batch by disposable { ModelBatch(shaderProvider) }


    context(camera: Camera, environment: Environment)
    fun render(instances: List<EnvModelInstance>) {
        batch.begin(camera)
        instances.forEach {
            batch.render(it.instance(), environment)
        }
        batch.end()
    }
}

fun foliageModelRenderer(
    windSystem: WindSystem,
    offsetProvider: OffsetProvider,
) =
    EnvironmentRenderer(foliageShaderProvider(offsetProvider, windSystem))

fun staticModelRenderer(
    offsetProvider: OffsetProvider,
) =
    EnvironmentRenderer(shiftingShaderProvider(offsetProvider))