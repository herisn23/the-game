package org.roldy.core.i18n

import org.roldy.core.i18n.format.LocalizedString

typealias I18NSource = Map<String, Map<String, String>>

interface MessageSource {
    val i18n: I18NSource
}

operator fun MessageSource.get(
    locale: String,
    key: String,
    defaultText: String = "__${locale}__${key}__"
) =
    i18n[locale]?.get(key) ?: defaultText


typealias LocalizedTextConfiguration = Map<String, LocalizedString>

class DefaultMessageSource(override val i18n: I18NSource) : MessageSource


fun main() {
//    context(context) {
//        val translated = t {
//            LocalizedStringProxy(config["compositeText"]!!, {
//                "MALY"
//            })
//        }
//        println(translated)
//    }
}