package com.cramsan.agentic.notification.fake

import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.Notifier
import com.cramsan.framework.logging.logD

private const val TAG = "FakeNotifier"

class FakeNotifier : Notifier {
    val receivedEvents: MutableList<AgenticEvent> = mutableListOf()

    override suspend fun notify(event: AgenticEvent) {
        logD(TAG, "notify called with event type: ${event::class.simpleName}")
        receivedEvents.add(event)
    }

    fun clear() {
        logD(TAG, "Clearing all received events (count=${receivedEvents.size})")
        receivedEvents.clear()
    }
}
