package org.roldy.core.i18n.format

import kotlin.enums.enumEntries


interface EnumCode {
    val code: String

    companion object {
        inline fun <reified T : Enum<T>> byCode(code: String?): T =
            enumEntries<T>().run {
                find { (it as EnumCode).code == code }
                    ?: throw Exception("Cannot find enum ${T::class}.$code. Available handlers [${
                        joinToString(
                            ", "
                        ) { (it as EnumCode).code }
                    }]")
            }
    }
}



inline fun <reified T : Enum<T>> ofCode(code: String) =
    EnumCode.byCode<T>(code)