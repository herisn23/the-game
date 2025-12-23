package org.roldy.core.i18n

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.I18NBundle
import io.github.classgraph.ClassGraph
import org.roldy.core.asset.loadAsset
import org.roldy.core.logger
import java.nio.file.Path
import java.util.Locale
import kotlin.properties.Delegates

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class I18NDsl

@I18NDsl
class I18N(
    defaultLocale: Locale = Locale.ENGLISH
) {


    var locale by Delegates.observable(defaultLocale) { _, _, newValue ->
        listeners.forEach {
            it()
        }
    }


    val languages by lazy {
        getLocalesFromClasspath()
    }

    private val bundles: Map<Locale, I18NBundle> by lazy {
        languages.associateWith { i ->
            I18NBundle.createBundle(asset, i)
        }
    }

    private val listeners: MutableList<() -> Unit> = mutableListOf()

    val asset by lazy { loadAsset("strings/i18n") }


    private fun getLocalesFromClasspath(): List<Locale> {
        val pattern = "i18n_([^.]+)\\.properties".toRegex()
        return ClassGraph()
            .acceptPaths("strings")
            .scan()
            .use { scanResult ->
                scanResult.allResources.mapNotNull { resource ->
                    pattern.find(resource.path)?.let {
                        Locale.forLanguageTag(it.groupValues[1])
                    }
                }
            }
    }

    operator fun get(key: Key): String =
        bundles.getValue(locale).format(key.key, *key.formatArguments)

    fun addOnLocaleChangedListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeOnLocaleChangedListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    class Key(val key: String, vararg val formatArguments: Any) {

        fun args(vararg args: Any) =
            Key(key = key, formatArguments = args)
    }

}