package com.codehavenx.alpaca.backend.service

import com.codehavenx.alpaca.backend.models.NotificationMessage
import com.codehavenx.alpaca.backend.models.UserId
import com.codehavenx.alpaca.backend.service.internalmessaging.InternalMessagingService
import com.codehavenx.alpaca.backend.service.internalmessaging.Message
import com.codehavenx.alpaca.backend.service.internalmessaging.Topic
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotificationService(
    private val internalMessagingService: InternalMessagingService,
    scope: CoroutineScope,
) {
    init {
        scope.launch {
            internalMessagingService.subscribe(Topic.NOTIFICATION).collect {
                handleMessage(it)
            }
        }
    }

    private fun handleMessage(message: Message) {
        logI(TAG, "Message received: $message")
        // val sendNotification = message as? Message.SendNotification ?: return
    }

    suspend fun sendNotification(userId: UserId, notificationMessage: NotificationMessage) {
        internalMessagingService.publish(
            Topic.NOTIFICATION,
            Message.SendNotification(
                userId,
                notificationMessage,
            ),
        )
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}
