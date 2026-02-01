package org.roldy.core.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider

//context(camera: Camera, environment: Environment)
//operator fun <T : RenderableProvider> ModelBatch.invoke(closure: () -> T) {
//    begin(camera)
//    render(closure(), environment)
//    end()
//}

@JvmName("invokeRenderables")
context(camera: Camera, environment: Environment)
operator fun <T : Iterable<A>, A : RenderableProvider> ModelBatch.invoke(closure: () -> T) {
    begin(camera)
    render(closure(), environment)
    end()
}