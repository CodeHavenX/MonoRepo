package com.codehavenx.platform.bot.service.internalmessaging

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InternalMessagingService {

    private val topicMap = mutableMapOf<Topic, MutableSharedFlow<Message>>()

    suspend fun publish(topic: Topic, message: Message) {
        ensureTopic(topic)
        topicMap[topic]?.emit(message)
    }

    fun subscribe(topic: Topic): SharedFlow<Message> {
        ensureTopic(topic)
        return requireNotNull(topicMap[topic]).asSharedFlow()
    }

    @Synchronized
    private fun ensureTopic(topic: Topic) {
        if (topicMap.containsKey(topic)) {
            return
        }
        topicMap[topic] = MutableSharedFlow()
    }
}
