package org.roldy.core

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

class Logger(
    val tag: String
) {

    enum class Level(val level: Int) {
        None(Application.LOG_NONE),
        Debug(Application.LOG_DEBUG),
        Info(Application.LOG_INFO),
        Error(Application.LOG_ERROR)
    }

    companion object {
        var level: Level
            get() = Level.entries.first { it.level == Gdx.app.logLevel }
            set(value) {
                Gdx.app.logLevel = value.level
            }
        val default = Logger("Default")
    }


    fun debug(message: String, ex: Throwable? = null) {
        when {
            ex != null -> Gdx.app.debug(tag, debugMessage(message), ex)
            else -> Gdx.app.debug(tag, debugMessage(message))
        }
    }

    fun debug(message: () -> String) {
        Gdx.app.debug(tag, debugMessage(message))
    }

    fun debug(ex: Throwable, message: () -> String) {
        Gdx.app.debug(tag, debugMessage(message), ex)
    }

    fun error(ex: Throwable, message: () -> String) {
        Gdx.app.error(tag, errorMessage(message), ex)
    }

    fun error(message: () -> String) {
        Gdx.app.error(tag, errorMessage(message))
    }

    fun error(message: String, ex: Throwable? = null) {
        when {
            ex != null -> Gdx.app.error(tag, errorMessage(message), ex)
            else -> Gdx.app.error(tag, errorMessage(message))
        }
    }

    fun info(ex: Throwable, message: () -> String) {
        Gdx.app.log(tag, infoMessage(message), ex)
    }

    fun info(message: () -> String) {
        Gdx.app.log(tag, infoMessage(message))
    }

    fun info(message: String, ex: Throwable? = null) {
        when {
            ex != null -> Gdx.app.log(tag, infoMessage(message), ex)
            else -> Gdx.app.log(tag, infoMessage(message))
        }
    }

    fun debugMessage(message: String) =
        "[DEBUG] $message"

    fun debugMessage(message: () -> String) =
        debugMessage(message())

    fun errorMessage(message: String) =
        "[ERROR] $message"

    fun errorMessage(message: () -> String) =
        errorMessage(message())

    fun infoMessage(message: String) =
        "[INFO] $message"

    fun infoMessage(message: () -> String) =
        infoMessage(message())
}

val logger by lazy {
    Logger.default
}

fun logger(tag: String): Lazy<Logger> = lazy {
    Logger(tag)
}

inline fun <reified T:Any> T.logger(): Lazy<Logger> = lazy {
    Logger(T::class.qualifiedName ?: "Unresolved")
}