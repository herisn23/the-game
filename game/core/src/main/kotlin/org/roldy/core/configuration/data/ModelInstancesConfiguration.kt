package org.roldy.core.configuration.data

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.roldy.core.configuration.G2DColorSerializer

@Serializable
data class ModelInstancesConfiguration(
    val materials: List<MaterialData>,
    val instances: List<ModelInstanceData>
) {

}

@Serializable
data class MaterialData(
    val materialName: String,
    val shaderName: String,
    val uniforms: List<Uniform>
)

@Serializable
sealed interface Uniform {
    val name: String
}

@Serializable
sealed interface FloatUniform : Uniform {
    val value: Float?
}

@Serializable
@SerialName("!RangeType")
data class RangeType(
    override val name: String,
    override val value: Float?
) : FloatUniform

@Serializable
@SerialName("!FloatType")
data class FloatType(
    override val name: String,
    override val value: Float?
) : FloatUniform

@Serializable
@SerialName("!ColorType")
data class ColorType(
    override val name: String,
    @Serializable(G2DColorSerializer::class)
    val value: Color?
) : Uniform

@Serializable
@SerialName("!TexEnvType")
data class TexEnvType(
    override val name: String,
    val value: String?
) : Uniform {

}

@Serializable
@SerialName("!VectorType")
data class VectorType(
    override val name: String,
    val value: String?
) : Uniform

@Serializable
data class ModelInstanceData(
    val modelName: String,
    val meshes: List<MeshData>
)

@Serializable
data class MeshData(
    val lod: Int,
    val meshName: String,
    val materialName: String
)