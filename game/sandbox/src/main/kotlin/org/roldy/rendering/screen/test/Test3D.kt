package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import org.roldy.core.DayNightCycle
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.postprocess.PostProcessing
import org.roldy.core.shader.shiftingShaderProvider
import org.roldy.core.system.ShadowSystem
import org.roldy.core.system.WindSystem
import org.roldy.core.utils.invoke
import org.roldy.editor.EditorCameraController
import org.roldy.g3d.AssetManagersLoader
import org.roldy.g3d.environment.EnvTexturesAssetAssetManager
import org.roldy.g3d.environment.TropicalAssetManager
import org.roldy.g3d.environment.property
import org.roldy.g3d.pawn.PawnManager
import org.roldy.g3d.pawn.PawnModelBuilder
import org.roldy.g3d.pawn.PawnRenderer
import org.roldy.g3d.skybox.Skybox

class Test3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {
    var loading = true
    val offsetProvider = object : OffsetProvider {
        override val shiftOffset: Vector3 = Vector3()
    }
    val windSystem = WindSystem()
    val postProcess = PostProcessing()
    val shadowSystem = ShadowSystem(
        object : OffsetProvider {
            override val shiftOffset: Vector3 = Vector3()
        },
        camera,
        windSystem = windSystem
    )
    val dayCycle = DayNightCycle(shadowSystem.environment, shadowSystem.shadowLight)
    val skybox by lazy { Skybox(dayCycle) }

    val emissive by disposable { EnvTexturesAssetAssetManager.tropicalEmissive01.get() }
    val diffuse by disposable { EnvTexturesAssetAssetManager.tropicalDiffuse01.get() }
    val tropicalModel by lazy {
        TropicalAssetManager.bldGiantColumn01.property(diffuse, emissive).apply {
            transform.setTranslation(10f, 0f, 0f)
        }
    }
    val modelBuilder by disposable(::PawnModelBuilder)
    val character by disposable {
        PawnManager(modelBuilder).apply {
            repeat(20) {
                cycleSets()
            }
        }.run {
            PawnRenderer(this)
        }
//        ModelInstance(PawnAssetManager.modelMale.get())
    }
    val batch by disposable { ModelBatch() }

    val groundPlate = SimplePlate.create(width = 500f, depth = 500f, color = Color.DARK_GRAY)
    val groundInstance = ModelInstance(groundPlate).apply {
        transform.setTranslation(0f, 0f, 0f)
    }

    val cameraController by lazy {
        EditorCameraController(camera)
    }

    val envModelBatch by disposable { ModelBatch(shiftingShaderProvider(offsetProvider)) }

    val plateModelBatch by disposable { ModelBatch() }

    val adapter by lazy {
        InputMultiplexer().apply {
//            addProcessor(RTSInputHandler(camera, TerrainRaycaster(heightSampler, camera), charController))
            addProcessor(cameraController)
        }
            .also(Gdx.input::setInputProcessor)
    }

    override fun render(delta: Float) {
        if (loading && AssetManagersLoader.update()) {
            loading = false
            adapter
        }
        if (loading) return


        context(delta, camera) {

            cameraController.update()

            shadowSystem {
                render(character.manager.instance)
                render(tropicalModel)
            }

            postProcess {
                skybox.render()
                context(shadowSystem.environment) {
                    plateModelBatch {
                        listOf(groundInstance)
                    }
                    envModelBatch {
                        listOf(tropicalModel)
                    }
                    character.render()
                }
            }
        }
    }
}


object SimplePlate {

    /**
     * Creates a simple flat rectangular plate/ground mesh
     *
     * @param width Width in X direction
     * @param depth Depth in Z direction
     * @param color Surface color
     * @return Model of a flat plate at Y=0
     */
    fun create(width: Float = 10f, depth: Float = 10f, color: Color = Color.GRAY): Model {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()

        val material = Material(ColorAttribute.createDiffuse(color))

        // Create a box with very small height to make it flat
        val meshPartBuilder = modelBuilder.part(
            "plate",
            GL20.GL_TRIANGLES,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong(),
            material
        )

        // Create a flat rectangle at Y=0
        meshPartBuilder.rect(
            Vector3(-width / 2, 0f, -depth / 2),  // Bottom-left
            Vector3(-width / 2, 0f, depth / 2),   // Top-left
            Vector3(width / 2, 0f, depth / 2),    // Top-right
            Vector3(width / 2, 0f, -depth / 2),   // Bottom-right
            Vector3(0f, 1f, 0f)               // Normal pointing up
        )

        return modelBuilder.end()
    }

    /**
     * Creates a subdivided ground plane (useful for receiving shadows better)
     *
     * @param width Width in X direction
     * @param depth Depth in Z direction
     * @param subdivisionsX Number of subdivisions along X axis
     * @param subdivisionsZ Number of subdivisions along Z axis
     * @param color Surface color
     */
    fun createSubdivided(
        width: Float = 10f,
        depth: Float = 10f,
        subdivisionsX: Int = 10,
        subdivisionsZ: Int = 10,
        color: Color = Color.GRAY
    ): Model {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()

        val material = Material(ColorAttribute.createDiffuse(color))

        val meshPartBuilder = modelBuilder.part(
            "plate",
            GL20.GL_TRIANGLES,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong(),
            material
        )

        val stepX = width / subdivisionsX
        val stepZ = depth / subdivisionsZ
        val halfWidth = width / 2f
        val halfDepth = depth / 2f

        // Create a grid of quads
        for (z in 0 until subdivisionsZ) {
            for (x in 0 until subdivisionsX) {
                val x0 = -halfWidth + x * stepX
                val x1 = -halfWidth + (x + 1) * stepX
                val z0 = -halfDepth + z * stepZ
                val z1 = -halfDepth + (z + 1) * stepZ

                // Create quad (two triangles)
                meshPartBuilder.rect(
                    Vector3(x0, 0f, z0),
                    Vector3(x0, 0f, z1),
                    Vector3(x1, 0f, z1),
                    Vector3(x1, 0f, z0),
                    Vector3(0f, 1f, 0f)
                )
            }
        }

        return modelBuilder.end()
    }
}

// USAGE EXAMPLE:

/*
// Simple plate:
val groundPlate = SimplePlate.create(width = 20f, depth = 20f, color = Color.DARK_GRAY)
val groundInstance = ModelInstance(groundPlate)

// Subdivided plate (better for shadows):
val groundPlate = SimplePlate.createSubdivided(
    width = 50f,
    depth = 50f,
    subdivisionsX = 20,
    subdivisionsZ = 20,
    color = Color.DARK_GRAY
)
val groundInstance = ModelInstance(groundPlate)

// Position it:
groundInstance.transform.setTranslation(0f, 0f, 0f)

// Render it:
modelBatch.render(groundInstance, environment)
*/






