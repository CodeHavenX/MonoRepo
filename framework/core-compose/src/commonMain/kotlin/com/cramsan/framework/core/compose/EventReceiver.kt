package com.cramsan.framework.core.compose

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Interface for receiving events.
 */
interface EventReceiver<T> {

    /**
     * Receives an event.
     *
     * @param event The event to be received.
     */
    suspend fun push(event: T)
}

/**
 * Interface for emitting events.
 */
interface EventEmitter<T> {
    val events: Flow<T>
}

/**
 * A receiver that will throw an exception if it is used.
 *
 * This is used to indicate that no event bus is available, and should not be used.
 */
class InvalidEventBus<T> : EventReceiver<T>, EventEmitter<T> {
    override val events: Flow<T>
        get() = error("Do not use this receiver.")

    override suspend fun push(event: T) {
        error("Do not use this receiver.")
    }
}

/**
 * A receiver that uses a [MutableSharedFlow] to emit events.
 */
open class EventBus<T>(
    private val sharedFlow: MutableSharedFlow<T> = MutableSharedFlow(),
) : EventReceiver<T>, EventEmitter<T> {

    override val events: Flow<T>
        get() = sharedFlow.asSharedFlow()

    override suspend fun push(event: T) {
        sharedFlow.emit(event)
    }
}
