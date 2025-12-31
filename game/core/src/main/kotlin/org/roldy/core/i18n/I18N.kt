package org.roldy.core.i18n

import com.badlogic.gdx.utils.I18NBundle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import io.github.classgraph.ClassGraph
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.roldy.core.asset.loadAsset
import org.roldy.core.i18n.format.*
import java.io.File
import kotlin.properties.Delegates

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class I18NDsl

@Serializable
data class I18NConfig(
    val references: Map<String, List<String>>,
    val strings: Map<String, LocalizedString>
)

@I18NDsl
class I18N(
    defaultLocale: String = "en"
) {

    init {
        I18NBundle.setExceptionOnMissingKey(false)
    }


    class DefaultContext(
        override val messageSource: MessageSource,
        override val genus: () -> Genus?,
        override val locale: () -> String,
        override val selection: () -> String?
    ) : ILocalizedContext

    var locale by Delegates.observable({ defaultLocale }) { _, _, newValue ->
        listeners.forEach {
            it()
        }
    }

    val stringsConfig by lazy {
        val strings = File("assets/i18n_config.yaml").readText()
        val yaml = Yaml(
            configuration = YamlConfiguration(
                allowAnchorsAndAliases = true
            )
        )
        yaml.decodeFromString<I18NConfig>(strings)
    }


    val languages by lazy {
        getLocalesFromClasspath()
    }

    private val messageSource: DefaultMessageSource by lazy {
        val all = languages.associateWith { i ->
            Yaml.default.parseToYamlNode(loadAsset("strings/strings_$i.yaml").readString()).yamlMap.entries
                .map { (messageKey, localization) ->
                    messageKey.content to localization.yamlScalar.content
                }.toMap()
        }
        DefaultMessageSource(all)
    }

    val listeners: MutableList<() -> Unit> = mutableListOf()


    private fun getLocalesFromClasspath(): List<String> {
        val pattern = "strings_([^.]+)\\.yaml".toRegex()
        return ClassGraph()
            .acceptPaths("strings")
            .scan()
            .use { scanResult ->
                scanResult.allResources.mapNotNull { resource ->
                    pattern.find(resource.path)?.groupValues[1]
                }
            }
    }

    operator fun get(key: Key): String {
        val ctx = DefaultContext(messageSource, { key.genus }, locale, { key.selection })
        return with(ctx) {
            translate {
                LocalizedStringProxy(stringsConfig.strings.getValue(key.key)) {
                    key.arguments[it.key] ?: "_MISSING_ARGUMENT_${it.key}_"
                }
            }
        }
    }

    fun selections(key: Strings.() -> Key): List<String> =
        selections(Strings.key())

    fun selections(key: Key): List<String> =
        when (val handler = stringsConfig.strings.getValue(key.key).handler) {
            is Selection -> handler.selections.map { get(key[it]) }
            else -> emptyList()
        }

    context(ctx: T)
    fun <T : ILocalizedContext> translate(block: StringProvider<T>): String =
        block.translate(ctx)

    fun addOnLocaleChangedListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeOnLocaleChangedListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    class Key(
        val key: String,
        val arguments: Map<String, String> = emptyMap(),
        val genus: Genus? = null,
        val selection: String? = null
    ) {


        fun genus(genus: Genus) =
            Key(key = key, arguments = arguments, genus = genus, selection = selection)

        inline fun <reified V> arg(key: String, value: V): Key {
            val arguments = mapOf(key to value.toString()) + arguments
            return Key(key = this.key, arguments = arguments, genus = genus, selection = selection)
        }

        operator fun get(selection: String) =
            Key(key = key, arguments = arguments, genus = genus, selection = selection)

        operator fun invoke(arguments: Map<String, String>) =
            Key(key = key, genus = genus, arguments = arguments, selection = selection)
    }

}