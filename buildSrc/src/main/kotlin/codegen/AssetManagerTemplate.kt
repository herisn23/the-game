package codegen

data class AssetData(
    val property: String,
    val path: String,
    val type: String
)

fun assetTemplate(
    pack: String,
    prefix: String,
    asssets: List<AssetData>,
    parent: String = "AssetManagerLoader",
    imports: List<String> = listOf(),
    configureAssetLoader: String = "",
    postProcess: () -> String = { "" }
) =
    """
package $pack
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import org.roldy.core.asset.Asset
import org.roldy.core.asset.AssetManagerLoader
${imports.joinToString("\n")}
object ${prefix}AssetManager: ${parent} {
        class ${prefix}Asset<T>(
         internal val path: String
        ):Asset<T> {
            override fun get(): T = assetManager.get<T>(path)
        }
        ${
        asssets.joinToString("\n") {
            "val ${it.property} = ${prefix}Asset<${it.type}>(\"${it.path}\")"
        }
    }
    override val assetManager by lazy {
        AssetManager().apply {
            ${configureAssetLoader}
            ${
        asssets.joinToString("\n") {
            assetLoadTemplate("${it.property}.path", it.type)
        }
    }
        }
    }
    ${postProcess()}
}
    """.trimIndent()

fun assetLoadTemplate(path: String, type: String) =
    "load($path, $type::class.java)"