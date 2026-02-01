package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import org.roldy.core.EnvironmentalRenderable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.shaderProvider
import org.roldy.g3d.ModelRenderer

class PawnRenderer(
    val manager: PawnManager
) : AutoDisposableAdapter(), EnvironmentalRenderable {
    val modelRenderer by disposable {
        ModelRenderer(shaderProvider {
            PawnShader(manager, it)
        }, instance = manager.instance)
    }

    context(delta: Float, environment: Environment, camera: Camera)
    override fun render() {
        manager.update()
        modelRenderer.render()
    }
}