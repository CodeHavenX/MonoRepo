package com.cramsan.agentic.notification.fake

import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.Notifier
import com.cramsan.framework.logging.logD

private const val TAG = "FakeNotifier"

/**
 * In-memory [com.cramsan.agentic.notification.Notifier] for tests. All delivered events are
 * appended to [receivedEvents] in delivery order. Use [clear] between test cases to reset state.
 *
 * Not thread-safe: [receivedEvents] is a plain [MutableList]. Tests that dispatch notifications
 * from multiple coroutines concurrently should synchronise access externally.
 */
class FakeNotifier : Notifier {
    val receivedEvents: MutableList<AgenticEvent> = mutableListOf()

    override suspend fun notify(event: AgenticEvent) {
        logD(TAG, "notify called with event type: ${event::class.simpleName}")
        receivedEvents.add(event)
    }

    /** Removes all recorded events. Call between test cases to prevent state leakage. */
    fun clear() {
        logD(TAG, "Clearing all received events (count=${receivedEvents.size})")
        receivedEvents.clear()
    }
}
