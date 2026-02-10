package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.model.Node
import org.roldy.core.EnvironmentalRenderable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.PawnShader
import org.roldy.core.shader.ShaderAttributes
import org.roldy.core.shader.util.shaderProvider
import org.roldy.g3d.ModelRenderer


class PawnRenderer(
    val manager: PawnManager
) : AutoDisposableAdapter(), EnvironmentalRenderable {
    val modelRenderer by disposable {
        ModelRenderer(shaderProvider {
            PawnShader(PawnShaderConfiguration(manager), it)
        }, instance = manager.instance)
    }

    context(delta: Float, environment: Environment, camera: Camera)
    override fun render() {
        manager.update()
        modelRenderer.render()
    }

    private class PawnShaderConfiguration(val manager: PawnManager) : PawnShader.Configuration {
        private val instance by lazy { manager.instance }
        override val texture0: Texture = PawnAssetManager.mask0.get()
        override val texture1: Texture = PawnAssetManager.mask1.get()
        override val texture2: Texture = PawnAssetManager.mask2.get()
        override val texture3: Texture = PawnAssetManager.mask3.get()
        override val texture4: Texture = PawnAssetManager.mask4.get()
        override val texture5: Texture = PawnAssetManager.mask5.get()
        override val texture6: Texture = PawnAssetManager.mask6.get()
        override val texture7: Texture = PawnAssetManager.mask7.get()
        override fun attributes(node: Node): ShaderAttributes =
            manager.getShaderConfig(node)

        override fun node(meshPart: MeshPart): Node =
            instance.meshMap.getValue(with(instance) { meshPart.mappingId() })
    }
}