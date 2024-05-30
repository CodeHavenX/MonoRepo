package com.codehavenx.platform.bot.service

import com.codehavenx.platform.bot.domain.models.NotificationMessage
import com.codehavenx.platform.bot.domain.models.UserId
import com.codehavenx.platform.bot.service.internalmessaging.InternalMessagingService
import com.codehavenx.platform.bot.service.internalmessaging.Message
import com.codehavenx.platform.bot.service.internalmessaging.Topic
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
