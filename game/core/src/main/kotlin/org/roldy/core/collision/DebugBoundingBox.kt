package org.roldy.core.collision

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.collision.BoundingBox

object DebugBoundingBox {

    fun create(boundingBox: BoundingBox, color: Color = Color.GREEN): ModelInstance {
        val builder = ModelBuilder()
        builder.begin()

        val material = Material(ColorAttribute.createDiffuse(color))
        val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()

        val w = boundingBox.width
        val h = boundingBox.height
        val d = boundingBox.depth

        val thickness = 0.02f

        val mpb = builder.part("box", GL20.GL_TRIANGLES, attributes, material)

        // 12 edges of a box as thin boxes
        // Bottom face edges
        mpb.box(0f, 0f, 0f, w, thickness, thickness)          // front bottom
        mpb.box(0f, 0f, d, w, thickness, thickness)            // back bottom
        mpb.box(-w / 2, 0f, 0f, thickness, thickness, d)         // left bottom
        mpb.box(w / 2, 0f, 0f, thickness, thickness, d)          // right bottom

        // Top face edges
        mpb.box(0f, h, 0f, w, thickness, thickness)            // front top
        mpb.box(0f, h, d, w, thickness, thickness)             // back top
        mpb.box(-w / 2, h, 0f, thickness, thickness, d)          // left top
        mpb.box(w / 2, h, 0f, thickness, thickness, d)           // right top

        // Vertical edges
        mpb.box(-w / 2, h / 2, 0f, thickness, h, thickness)        // front-left
        mpb.box(w / 2, h / 2, 0f, thickness, h, thickness)         // front-right
        mpb.box(-w / 2, h / 2, d, thickness, h, thickness)         // back-left
        mpb.box(w / 2, h / 2, d, thickness, h, thickness)          // back-right

        val model = builder.end()
        return ModelInstance(model)
    }
}