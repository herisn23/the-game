package org.roldy.core.i18n

import com.badlogic.gdx.utils.I18NBundle
import org.roldy.core.asset.loadAsset
import java.util.Locale

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class I18NDsl

@I18NDsl
class I18N(
    val defaultLocale: Locale = Locale.of("en")
) {
    private lateinit var bundle: I18NBundle

    private val listeners: MutableList<() -> Unit> = mutableListOf()

    init {
        reload(defaultLocale)
    }

    fun reload(locale: Locale) {
        bundle = I18NBundle.createBundle(loadAsset("i18n"), locale)
        listeners.forEach { it() }
    }

    fun default() {
        reload(defaultLocale)
    }

    operator fun get(key: Key): String =
        bundle.format(key.key, *key.formatArguments)

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