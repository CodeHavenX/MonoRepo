package com.codehavenx.alpaca.backend.service.internalmessaging

import com.codehavenx.alpaca.backend.models.NotificationMessage
import com.codehavenx.alpaca.backend.models.UserId

sealed class Message {

    data class SendNotification(
        val userId: UserId,
        val message: NotificationMessage,
    ) : Message()
}
