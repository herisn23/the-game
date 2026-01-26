package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.roldy.core.EnvironmentalRenderable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable

class PawnRenderer(
    val manager: PawnManager
) : AutoDisposableAdapter(), EnvironmentalRenderable {

    val batch by disposable { ModelBatch(PawnShaderProvider(manager)) }

    context(delta: Float, environment: Environment, camera: Camera)
    override fun render() {
        manager.update()
        batch.begin(camera)
        batch.render(manager.instance, environment)
        batch.end()
    }
}