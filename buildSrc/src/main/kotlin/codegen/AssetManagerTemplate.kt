package codegen

data class AssetData(
    val property: String,
    val path: String,
    val type: String,
    val key: String = property
)

fun assetTemplate(
    pack: String,
    prefix: String,
    assets: List<AssetData>,
    applyLoaders: Boolean = true,
    parent: String = "AssetManagerLoader",
    imports: List<String> = listOf(),
    configureAssetLoader: String = "",
    assetLoaderParameters: String = "",
    postProcess: () -> String = { "" }
) =
    """
package $pack
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import org.roldy.core.asset.Asset
import org.roldy.core.asset.AssetManagerLoader
import org.roldy.core.asset.initialize
import kotlin.reflect.KClass
${imports.joinToString("\n")}
object ${prefix}AssetManager: ${parent} {
        class ${prefix}Asset<T: Any>(
         val path: String,
         override val cls: KClass<T>
        ):Asset<T> {
            override fun get(): T = assetManager.get<T>(path).initialize()
            override fun load(assetManager: AssetManager) {
                assetManager.load(path, cls.java$assetLoaderParameters)
            }
        }
        ${
        assets.joinToString("\n") {
            "val ${it.property} = ${prefix}Asset(\"${it.path}\", ${it.type}::class)"
        }
    }
    override val assetManager by lazy {
        AssetManager().apply {
            ${configureAssetLoader}
            ${
        if (applyLoaders) {
            assets.joinToString("\n") {
                assetLoadTemplate(it.property)
            }
        } else ""
    }
        }
    }
    ${postProcess()}
}
    """.trimIndent()

fun assetLoadTemplate(path: String) =
    "${path}.load(this)"