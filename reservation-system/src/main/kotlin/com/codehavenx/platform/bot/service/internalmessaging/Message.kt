package com.codehavenx.platform.bot.service.internalmessaging

import com.codehavenx.platform.bot.domain.models.NotificationMessage
import com.codehavenx.platform.bot.domain.models.UserId

sealed class Message {

    data class SendNotification(
        val userId: UserId,
        val message: NotificationMessage,
    ) : Message()

}