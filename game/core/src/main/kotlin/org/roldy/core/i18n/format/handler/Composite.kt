package org.roldy.core.i18n.format.handler

import org.roldy.core.i18n.format.*
import org.roldy.core.i18n.get

fun LocalizedStringProxy.composite(
    context: ILocalizedContext
): String =
    CompositeLocalizedString(context.messageSource[context.locale(), localizedString.key]).run {
        arguments.map { (key, selector) ->
            key to when (selector.type) {
                //resolve value by Selector.Type
                CompositeLocalizedString.Selector.Type.LocalizedInflectionText -> {
                    val type = Inflection.ofCode(selector.arguments.first())
                    val inflectionText = LocalizedString(
                        selector.key,
                        type
                    )
                    copy(localizedString = inflectionText).inflection(context, type)
                }

                CompositeLocalizedString.Selector.Type.LocalizedText -> {
                    val text = LocalizedString(
                        selector.key,
                        Simple
                    )
                    copy(localizedString = text).simple(context)
                }

                CompositeLocalizedString.Selector.Type.Argument -> argumentDelegate?.invoke(
                    selector
                )

                CompositeLocalizedString.Selector.Type.CompositeText -> {
                    val inflectionText = LocalizedString(
                        selector.key,
                        Composite
                    )
                    copy(localizedString = inflectionText, argumentDelegate = argumentDelegate).composite(context)
                }
            }.run {
                fun List<BasicStringHandler>.chain(string: String?): String? =
                    when (isEmpty()) {
                        true -> string
                        else -> first().handle(string).let { str ->
                            subList(1, size).chain(str)
                        }
                    }

                selector.handlersChain.chain(this)
            }
        }.toMap().let(::resolve)
    }