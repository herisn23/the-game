package org.roldy.core.i18n.format


class CompositeLocalizedString(
    private val str: String
) {
    //escape second bracket is needed in android
    private val strRegexp = """(\{(.*?)\})""".toRegex()
    private val selectorRegexp = """((.*?)\((.*?)\))""".toRegex()

    data class Selector(
        val type: Type,
        val arguments: List<String>,
        val handlersChain: List<BasicStringHandler>,
        val key: String
    ) {
        enum class Type(override val code: String) : EnumCode {
            Argument("arg"),
            LocalizedInflectionText("lit"),
            CompositeText("ct"),
            LocalizedText("lt");
        }
    }

    val arguments: Map<String, Selector> by lazy {
        strRegexp.findAll(str).map { result ->
            val values = result.groupValues.toSet()
            val placeholder = values.first()
            val placeholderValue = values.last()
            placeholder to placeholderValue.resolveSelector()
        }.toMap()
    }

    private fun String.resolveSelector(): Selector =
        split(":").let { selectors ->
            when (selectors.size) {
                0, 1 -> throw Exception(
                    "wrong argument '$this' available arguments [${
                        Selector.Type.entries.joinToString(
                            ", "
                        ) { "${it.code}:$this" }
                    }]"
                )

                else -> {
                    val selectorType = selectors.first()
                    val key = selectors.last()
                    val basicStringHandlers =
                        selectors.filter { !listOf(selectorType, key).contains(it) }.map {
                            ofCode<BasicStringHandler>(it)
                        }

                    val (type, arguments) = selectorRegexp.findAll(selectorType).let { result ->
                        when (result.none()) {
                            true -> selectors[0] to null
                            false -> result.toList().map { it.groupValues }
                                .flatten()
                                .toSet()
                                .toList()
                                .filterIndexed { index, _ -> index != 0 } //exclude first index because its whole expression
                                .let { groupedSelector ->
                                    groupedSelector[0] to groupedSelector.run {
                                        subList(
                                            1,
                                            size
                                        )
                                    }
                                }
                        }
                    }
                    Selector(
                        ofCode<Selector.Type>(type),
                        arguments ?: emptyList(),
                        basicStringHandlers,
                        key
                    )
                }
            }
        }

    fun resolve(arguments: Map<String, String?>) =
        strRegexp.replace(str) { result ->
            arguments[result.value] ?: "__${result.value}__"
        }
}