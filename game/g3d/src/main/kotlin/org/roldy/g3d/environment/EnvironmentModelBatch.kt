package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.foliageShaderProvider
import org.roldy.core.shader.shiftingShaderProvider
import org.roldy.core.system.WindSystem

class EnvironmentModelBatch(
    shaderProvider: ShaderProvider
) : AutoDisposableAdapter() {

    val foliageBatch by disposable { ModelBatch(shaderProvider) }


    context(camera: Camera)
    fun render(instances: List<EnvModelInstance>) {
        instances.forEach {
            foliageBatch.render(it.instance())
        }
    }
}

fun foliageModelBatch(
    windSystem: WindSystem,
    offsetProvider: OffsetProvider,
) =
    EnvironmentModelBatch(foliageShaderProvider(offsetProvider, windSystem))

fun staticModelBatch(
    offsetProvider: OffsetProvider,
) =
    EnvironmentModelBatch(shiftingShaderProvider(offsetProvider))