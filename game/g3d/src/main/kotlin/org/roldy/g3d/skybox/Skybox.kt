package org.roldy.g3d.skybox

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.Matrix4
import org.roldy.core.CameraRenderable
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable

class Skybox : AutoDisposableAdapter(), CameraRenderable {
    private val cubemap: Cubemap by disposable {
        fun load(side: String) = loadAsset("3d/skybox/CosmicCoolCloud$side.hdr")
        Cubemap(
            load("Left"),   // +X
            load("Right"),    // -X
            load("Top"),     // +Y
            load("Bottom"),  // -Y
            load("Front"),   // +Z
            load("Back")     // -Z
        )
    }

    private val shader by disposable {
        ShaderLoader.skyboxShader
    }

    private val mesh by disposable {
        val vertices = floatArrayOf(
            -1f, 1f, -1f, -1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f, -1f,
            -1f, -1f, 1f, -1f, -1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f,
            1f, -1f, -1f, 1f, -1f, 1f, 1f, 1f, 1f, 1f, 1f, -1f,
            -1f, -1f, 1f, -1f, 1f, 1f, 1f, 1f, 1f, 1f, -1f, 1f,
            -1f, 1f, -1f, 1f, 1f, -1f, 1f, 1f, 1f, -1f, 1f, 1f,
            -1f, -1f, -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f, -1f, -1f
        )
        val indices = shortArrayOf(
            0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4, 8, 9, 10, 10, 11, 8,
            12, 13, 14, 14, 15, 12, 16, 17, 18, 18, 19, 16, 20, 21, 22, 22, 23, 20
        )
        Mesh(true, 24, 36, VertexAttribute.Position()).apply {
            setVertices(vertices)
            setIndices(indices)
        }
    }

    context(_: Float, camera: Camera)
    override fun render() {
        val view = camera.view.cpy()
        view.`val`[Matrix4.M03] = 0f
        view.`val`[Matrix4.M13] = 0f
        view.`val`[Matrix4.M23] = 0f
        val projView = camera.projection.cpy().mul(view)

        // Enable depth writing and set to far plane
        Gdx.gl.glDepthMask(true)  // Changed: enable depth write
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)  // Added: allow writing at max depth
        Gdx.gl.glDisable(GL20.GL_CULL_FACE)

        cubemap.bind(0)
        shader.bind()
        shader.setUniformMatrix("u_projView", projView)
        shader.setUniformi("u_cubemap", 0)
        mesh.render(shader, GL20.GL_TRIANGLES)

        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
        Gdx.gl.glDepthFunc(GL20.GL_LESS)  // Restore default
    }
}