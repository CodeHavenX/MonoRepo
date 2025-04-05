package com.cramsan.framework.core.compose

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow

/**
 * Interface for receiving application-wide events.
 */
interface ApplicationEventReceiver {

    /**
     * Receives an application event.
     *
     * @param event The application event to be received.
     */
    suspend fun receiveApplicationEvent(event: ApplicationViewModelEvent)
}

/**
 * Interface for emitting application-wide events.
 */
interface ApplicationEventEmitter {
    val events: Flow<ApplicationViewModelEvent>
}

/**
 * A no-operation implementation of [ApplicationEventReceiver].
 * This is used when no action is needed for the event.
 */
class NoopApplicationEventReceiver : ApplicationEventReceiver, ApplicationEventEmitter {
    override suspend fun receiveApplicationEvent(event: ApplicationViewModelEvent) {
        // No operation
    }

    override val events: Flow<ApplicationViewModelEvent>
        get() = flow { }
}

/**
 * A receiver that throws an exception when an event is received.
 * This is used to indicate that the receiver should not be used.
 */
class InvalidApplicationEventReceiver : ApplicationEventReceiver, ApplicationEventEmitter {
    override suspend fun receiveApplicationEvent(event: ApplicationViewModelEvent) {
        error("Do not use this receiver.")
    }

    override val events: Flow<ApplicationViewModelEvent>
        get() = error("Do not use this receiver.")
}

/**
 * A receiver that uses a [MutableSharedFlow] to emit application events.
 * This is used to share events between different parts of the application.
 */
class SharedFlowApplicationReceiver(
    private val sharedFlow: MutableSharedFlow<ApplicationViewModelEvent> = MutableSharedFlow(),
) : ApplicationEventReceiver, ApplicationEventEmitter {

    override val events: Flow<ApplicationViewModelEvent>
        get() = sharedFlow.asSharedFlow()

    override suspend fun receiveApplicationEvent(event: ApplicationViewModelEvent) {
        sharedFlow.emit(event)
    }
}
