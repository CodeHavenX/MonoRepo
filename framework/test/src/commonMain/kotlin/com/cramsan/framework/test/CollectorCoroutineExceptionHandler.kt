package com.cramsan.framework.test

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * A CoroutineExceptionHandler that collects exceptions that are thrown during the execution of a coroutine.
 */
class CollectorCoroutineExceptionHandler :
    AbstractCoroutineContextElement(
        CoroutineExceptionHandler.Key,
    ),
    CoroutineExceptionHandler {

    private val _exceptions = mutableListOf<Throwable>()
    val exceptions: List<Throwable>
        get() = _exceptions.toList()

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        println("Exception detected by CollectorCoroutineExceptionHandler")
        exception.printStackTrace()
        _exceptions.add(exception)
    }

    /**
     * Clears the list of exceptions that have been collected.
     */
    fun clearExceptions() {
        _exceptions.clear()
    }
}
