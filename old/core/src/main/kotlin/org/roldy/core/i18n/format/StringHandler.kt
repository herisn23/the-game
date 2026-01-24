package org.roldy.core.i18n.format

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale.getDefault


@Serializable
sealed interface StringHandler

@Serializable
sealed interface LocalizedStringHandler : StringHandler

enum class BasicStringHandler(override val code: String) : StringHandler, EnumCode {
    Lower("lower"),
    Upper("upper"),
    Capitalize("cap"),
    Decapitalize("decap"),
    Trim("trim"),
    TrimEnd("trime"),
    TrimStart("trims")

}

fun BasicStringHandler.handle(text: String?): String? =
    when (this) {
        BasicStringHandler.Lower -> text?.lowercase()
        BasicStringHandler.Upper -> text?.uppercase()
        BasicStringHandler.Capitalize -> text?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
        BasicStringHandler.Decapitalize -> text?.replaceFirstChar { it.lowercase(getDefault()) }
        BasicStringHandler.Trim -> text?.trim()
        BasicStringHandler.TrimEnd -> text?.trimEnd()
        BasicStringHandler.TrimStart -> text?.trimStart()
    }

@Serializable
@SerialName("inflection")
class Inflection(
    val type: InflectionType
) : LocalizedStringHandler {
    @Serializable
    sealed interface InflectionType : StringHandler


    @Serializable
    @SerialName("genus")
    data object Genus : InflectionType

    companion object {

        private val genus by lazy {
            Genus.serializer().value
        }

        fun ofCode(code: String): Inflection =
            Inflection(
                when (code) {
                    genus -> Genus
                    else -> throw Exception("Cannot resolve InflectionType for code $code")
                }
            )

        @OptIn(ExperimentalSerializationApi::class)
        val <T : InflectionType> KSerializer<T>.value
            get() =
                descriptor.serialName
    }
}

@Serializable
@SerialName("simple")
data object Simple : LocalizedStringHandler

@Serializable
@SerialName("composite")
data object Composite : LocalizedStringHandler

@Serializable
@SerialName("selection")
class Selection(
    val selections: List<String>
) : LocalizedStringHandler