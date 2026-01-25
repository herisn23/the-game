package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.utils.sequencer
import org.roldy.g3d.pawn.PawnAnimations
import org.roldy.g3d.pawn.PawnAssetManager
import org.roldy.g3d.pawn.PawnConfiguration
import org.roldy.g3d.pawn.PawnShaderProvider


class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {
    var loading = true

    init {
        camera.position.set(16.799997f, 0f, 0f)
        camera.lookAt(0f, 0f, 0f)
        camera.update()
    }

    val orig by lazy {
        PawnAssetManager.model.get().apply {
            printModelData()
        }
    }

    fun Model.printModelData() {
        fun printBoneOrder(node: Node, indent: String = "") {
            if (node.id == "Root") return
            if (node.children.count() == 0) {
                val parents = mutableListOf<Node>()
                fun Node.parentPath() {
                    if (parent != null) {
                        parents.add(parent)
                        parent.parentPath()
                    }
                }
                node.parentPath()
                print(parents.reversed().joinToString("|") { it.id })
                println("|${node.id}")
            }

            node.children.forEach { printBoneOrder(it, "$indent  ") }
        }
        nodes.forEach { printBoneOrder(it) }
    }

    val character by lazy {
        PawnConfiguration(orig).apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(0f, -9.700001f, 0f)
            initialTransform.rotate(Vector3.Y, 90f)
            instance.transform.set(initialTransform) // Copy it over
        }
    }
    val animController by lazy {
        AnimationController(character.instance).apply {
            setAnimation(PawnAnimations.idle.id, -1)
        }
    }
    val batch by disposable { ModelBatch(PawnShaderProvider(character)) }


    val env by lazy {
        Environment().apply {
            set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }
    }

    fun printNodeHierarchy(node: Node, indent: String = "") {
        println("$indent${node.id}")
        for (child in node.children) {
            printNodeHierarchy(child, "$indent  ")
        }
    }

    val controller by lazy {
        ModelController(character.instance, camera).also(Gdx.input::setInputProcessor).apply {

        }
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    val anims by sequencer {
        PawnAnimations.all.keys.toList()
    }
    val colors by sequencer {
        listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
    }
    val parts by sequencer {
        listOf(
            character.colorPrimary,
            character.colorSecondary,
            character.colorLeatherPrimary,
            character.colorLeatherSecondary,
            character.colorMetalPrimary,
            character.colorMetalSecondary,
            character.colorMetalDark,
            character.colorHair,
            character.colorSkin,
            character.colorStubble,
            character.colorScar,
            character.colorBodyArt,
            character.colorEyes
        )
    }
    val rotation = Quaternion()
    val allowed by sequencer {

        listOf(
            emptyList(),
            character.allNodes.map { it.id },
            listOf(
                "Chr_Hips_Male_00",
                "Chr_HandLeft_Male_00",
                "Chr_HandRight_Male_00",
                "Chr_LegLeft_Male_00",
                "Chr_LegRight_Male_00",
                "Chr_ArmLowerLeft_Male_00",
                "Chr_ArmLowerRight_Male_00",
                "Chr_ArmUpperLeft_Male_00",
                "Chr_ArmUpperRight_Male_00",
                "Chr_Torso_Male_00",
                "Chr_FacialHair_Male_01",
                "Chr_Head_Male_19",
            ),
            listOf(
                "Chr_Hips_Male_00",
                "Chr_HandLeft_Male_00",
                "Chr_HandRight_Male_00",
                "Chr_LegLeft_Male_00",
                "Chr_LegRight_Male_00",
                "Chr_ArmLowerRight_Male_11",
                "Chr_ArmLowerRight_Male_00",
                "Chr_ArmUpperLeft_Male_00",
                "Chr_ArmUpperRight_Male_00",
                "Chr_Torso_Male_00",
                "Chr_FacialHair_Male_01",
                "Chr_Eyebrow_Male_01",
                "Chr_Head_Male_19",
                "Chr_HeadCoverings_No_Hair_09"
            )
        )
    }

    override fun render(delta: Float) {
        if (loading && PawnAssetManager.assetManager.update()) {
            loading = false

        }
        if (loading) return
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight)
        Gdx.gl.glClearColor(1f, 0f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            parts.next()
            println(parts.current)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            character.colorBodyArt.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            character.colorSkin.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            character.colorEyes.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            character.setVisibility(
                allowed.next()
            )
        }


        camera.update()

        controller.update()
        animController.update(delta)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            animController.setAnimation(anims.next(), -1)
        }

        batch.begin(camera)


        batch.render(character.instance, env)
        batch.end()
    }

}

class ModelController(
    val model: ModelInstance,
    val camera: PerspectiveCamera
) : InputAdapter() {
    var position: Vector3 = model.transform.getTranslation(Vector3())
    private var zoom = 10f // Distance from target
    var lastKey = -1
    override fun keyDown(keycode: Int): Boolean {
        lastKey = keycode
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        lastKey = -1
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // Zoom in/out
        zoom += amountY * 2f // Adjust multiplier for zoom speed
        zoom = zoom.coerceIn(2f, 500f) // Clamp between min and max distance
        updateCameraPosition()
        return true
    }

    private fun updateCameraPosition() {
        // Move camera along its current direction
        val direction = Vector3(camera.direction).nor()
        camera.position.set(0f, 0f, 0f).add(direction.scl(-zoom))
        camera.lookAt(0f, 0f, 0f)
        camera.update()

    }

    fun update() {
        when (lastKey) {
            Input.Keys.W -> {
                position.y += 0.1f
            }

            Input.Keys.S -> {
                position.y -= 0.1f
            }

            Input.Keys.Q -> {
                model.transform.rotate(Vector3.Y, 1f)
            }

            Input.Keys.E -> {
                model.transform.rotate(Vector3.Y, -1f)
            }
        }
//        model.transform.setTranslation(position)
    }
}