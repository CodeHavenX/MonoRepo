package com.cramsan.agentic.notification

interface Notifier {
    suspend fun notify(event: AgenticEvent)
}
