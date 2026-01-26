package org.roldy.g3d

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import org.roldy.core.EnvironmentalRenderable
import org.roldy.core.disposable.AutoDisposableAdapter

class ModelRenderer(
    shader: ShaderProvider = DefaultShaderProvider(),
    batch: ModelBatch? = null,
    val instance: ModelInstance
) : AutoDisposableAdapter(), EnvironmentalRenderable {

    val batch = batch ?: ModelBatch(shader).disposable()

    context(delta: Float, environment: Environment, camera: Camera)
    override fun render() {
        batch.begin(camera)
        batch.render(instance, environment)
        batch.end()
    }
}