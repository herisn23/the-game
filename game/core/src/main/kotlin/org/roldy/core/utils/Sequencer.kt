package org.roldy.core.utils

class Sequencer<T>(
    val list: List<T>,
) {
    private var index = 0


    val current: T get() = list[index]

    fun next(block: T.(previous: T) -> Unit): Unit =
        nextIndex { previous ->
            list[index].block(previous)
        }

    fun next(): T =
        nextIndex {
            list[index]
        }

    private fun <R> nextIndex(exec: (T) -> R): R {
        val previous = current
        index++
        clampIndex()
        return exec(previous)
    }

    fun prev(block: T.(T) -> Unit): Unit =
        prevIndex { previous ->
            list[index].block(previous)
        }

    fun prev(): T =
        prevIndex {
            list[index]
        }

    private fun <R> prevIndex(exec: (T) -> R): R {
        val previous = current
        index--
        clampIndex()
        return exec(previous)
    }

    fun reset() {
        index = 0
    }

    fun clampIndex() {
        index = index.clamp(0, list.size)
    }
}

fun <T> sequencer(list: ()->List<T>) = lazy {
    Sequencer(list())
}

fun <T> sequencer(list: List<T>) = Sequencer(list)