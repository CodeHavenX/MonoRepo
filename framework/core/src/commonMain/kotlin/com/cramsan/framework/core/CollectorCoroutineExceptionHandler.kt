package com.cramsan.framework.core

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * A CoroutineExceptionHandler that collects exceptions that are thrown during the execution of a coroutine.
 */
class CollectorCoroutineExceptionHandler :
    AbstractCoroutineContextElement(
        CoroutineExceptionHandler,
    ),
    CoroutineExceptionHandler {

    private val _exceptions = mutableListOf<Throwable>()
    val exceptions: List<Throwable>
        get() = _exceptions.toList()

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        _exceptions.add(exception)
    }

    /**
     * Clears the list of exceptions that have been collected.
     */
    fun clearExceptions() {
        _exceptions.clear()
    }
}
