package com.cramsan.edifikana.server

import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.NotificationNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.server.controller.toNotificationNetworkResponse
import com.cramsan.edifikana.server.controller.toUserNetworkResponse
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.test.asClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

class NetworkMapperTest {

    @OptIn(NetworkModel::class)
    @Test
    fun `map user to network response`() {
        // Arrange
        val user = User(
            id = UserId("123"),
            email = "test@test.com",
            phoneNumber = "1234567890",
            firstName = "Test",
            lastName = "User",
            authMetadata = User.AuthMetadata(isPasswordSet = true),
            role = UserRole.SUPERUSER
        )
        val expectedResponse = UserNetworkResponse(
            id = "123",
            email = "test@test.com",
            phoneNumber = "1234567890",
            firstName = "Test",
            lastName = "User",
            authMetadata = AuthMetadataNetworkResponse(isPasswordSet = true)
        )

        // Act
        val response = user.toUserNetworkResponse()
        // Assert
        assertEquals(expectedResponse, response)
    }

    @OptIn(NetworkModel::class, ExperimentalTime::class)
    @Test
    fun `map notification to network response`() {
        // Arrange
        val testTimeSource = TestTimeSource()
        val clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        val createdAt = clock.now()

        val notification = Notification(
            notificationId = NotificationId("notif123"),
            recipientUserId = UserId("user123"),
            recipientEmail = "test@test.com",
            organizationId = OrganizationId("org123"),
            notificationType = NotificationType.INVITE,
            isRead = false,
            createdAt = createdAt,
            readAt = null,
        )
        val expectedResponse = NotificationNetworkResponse(
            notificationId = NotificationId("notif123"),
            organizationId = OrganizationId("org123"),
            notificationType = NotificationType.INVITE,
            isRead = false,
            createdAt = createdAt.epochSeconds,
            readAt = null,
        )

        // Act
        val response = notification.toNotificationNetworkResponse()

        // Assert
        assertEquals(expectedResponse, response)
    }

    @OptIn(NetworkModel::class, ExperimentalTime::class)
    @Test
    fun `map notification to network response with read timestamp`() {
        // Arrange
        val testTimeSource = TestTimeSource()
        val clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        val createdAt = clock.now()
        val readAt = clock.now()

        val notification = Notification(
            notificationId = NotificationId("notif123"),
            recipientUserId = UserId("user123"),
            recipientEmail = "test@test.com",
            organizationId = OrganizationId("org123"),
            notificationType = NotificationType.SYSTEM,
            isRead = true,
            createdAt = createdAt,
            readAt = readAt,
        )
        val expectedResponse = NotificationNetworkResponse(
            notificationId = NotificationId("notif123"),
            organizationId = OrganizationId("org123"),
            notificationType = NotificationType.SYSTEM,
            isRead = true,
            createdAt = createdAt.epochSeconds,
            readAt = readAt.epochSeconds,
        )

        // Act
        val response = notification.toNotificationNetworkResponse()

        // Assert
        assertEquals(expectedResponse, response)
    }
}
