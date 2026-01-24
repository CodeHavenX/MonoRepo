package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.NotificationService
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.asClock
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

@OptIn(ExperimentalTime::class)
class NotificationControllerTest : CoroutineTest(), KoinTest {

    private lateinit var testTimeSource: TestTimeSource

    @BeforeTest
    fun setupTest() {
        testTimeSource = TestTimeSource()
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    private fun createTestNotification(
        notificationId: NotificationId = NotificationId("notif123"),
        recipientUserId: UserId = UserId("user123"),
        recipientEmail: String = "test@example.com",
        organizationId: OrganizationId = OrganizationId("org123"),
        notificationType: NotificationType = NotificationType.INVITE,
        isRead: Boolean = false,
    ): Notification {
        val clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        return Notification(
            id = notificationId,
            recipientUserId = recipientUserId,
            recipientEmail = recipientEmail,
            organizationId = organizationId,
            notificationType = notificationType,
            isRead = isRead,
            createdAt = clock.now(),
            readAt = null,
        )
    }

    @Test
    fun `test getNotifications returns list of notifications for authenticated user`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val notifications = listOf(
            createTestNotification(NotificationId("notif1"), userId),
            createTestNotification(NotificationId("notif2"), userId),
        )

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotificationsForUser(userId) } returns Result.success(notifications)

        // Act
        val response = client.get("notification")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("notif1"))
        assertTrue(body.contains("notif2"))
        coVerify { notificationService.getNotificationsForUser(userId) }
    }

    @Test
    fun `test getNotifications returns empty list when no notifications`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotificationsForUser(userId) } returns Result.success(emptyList())

        // Act
        val response = client.get("notification")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("content"))
        assertTrue(body.contains("[]"))
    }

    @Test
    fun `test getNotification returns notification when owned by user`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val notification = createTestNotification(notificationId, userId)

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(notification)

        // Act
        val response = client.get("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("notif123"))
        assertTrue(body.contains("org123"))
        coVerify { notificationService.getNotification(notificationId) }
    }

    @Test
    fun `test getNotification returns 404 when notification not found`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(null)

        // Act
        val response = client.get("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test getNotification returns 403 when notification belongs to another user`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val otherUserId = UserId("otherUser")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val notification = createTestNotification(notificationId, otherUserId)

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(notification)

        // Act
        val response = client.get("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `test markAsRead marks notification as read when owned by user`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val notification = createTestNotification(notificationId, userId)
        val updatedNotification = createTestNotification(notificationId, userId, isRead = true)

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(notification)
        coEvery { notificationService.markAsRead(notificationId) } returns Result.success(updatedNotification)

        // Act
        val response = client.post("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        coVerify { notificationService.markAsRead(notificationId) }
    }

    @Test
    fun `test markAsRead returns 404 when notification not found`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(null)

        // Act
        val response = client.post("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test markAsRead returns 403 when notification belongs to another user`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val otherUserId = UserId("otherUser")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val notification = createTestNotification(notificationId, otherUserId)

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(notification)

        // Act
        val response = client.post("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `test deleteNotification deletes notification when owned by user`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val notification = createTestNotification(notificationId, userId)

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(notification)
        coEvery { notificationService.deleteNotification(notificationId) } returns Result.success(true)

        // Act
        val response = client.delete("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        coVerify { notificationService.deleteNotification(notificationId) }
    }

    @Test
    fun `test deleteNotification returns 404 when notification not found`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(null)

        // Act
        val response = client.delete("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test deleteNotification returns 403 when notification belongs to another user`() = testBackEndApplication {
        // Arrange
        val userId = UserId("user123")
        val otherUserId = UserId("otherUser")
        val notificationId = NotificationId("notif123")
        val notificationService = get<NotificationService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val notification = createTestNotification(notificationId, otherUserId)

        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = userId,
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(notification)

        // Act
        val response = client.delete("notification/notif123")

        // Assert
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
