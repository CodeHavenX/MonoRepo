package com.cramsan.agentic.notification.fake

import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.Notifier

class FakeNotifier : Notifier {
    val receivedEvents: MutableList<AgenticEvent> = mutableListOf()

    override suspend fun notify(event: AgenticEvent) {
        receivedEvents.add(event)
    }

    fun clear() {
        receivedEvents.clear()
    }
}
