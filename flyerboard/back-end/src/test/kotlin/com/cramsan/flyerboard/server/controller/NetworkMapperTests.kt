package com.cramsan.flyerboard.server.controller

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.datastore.SignedUpload
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.flyerboard.server.service.models.User
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class NetworkMapperTests {
    private fun makeFlyer(
        id: String = "flyer-1",
        status: FlyerStatus = FlyerStatus.PENDING,
        expiresAt: Instant? = null,
        fileUrl: String? = "https://signed.example.com/file.png",
        rejectionReason: String? = null,
    ) = Flyer(
        id = FlyerId(id),
        title = "Test Flyer",
        description = "Test Description",
        filePath = "uploads/file.png",
        status = status,
        expiresAt = expiresAt,
        uploaderId = UserId("user-1"),
        createdAt = Instant.fromEpochSeconds(0),
        updatedAt = Instant.fromEpochSeconds(1),
        fileUrl = fileUrl,
        rejectionReason = rejectionReason,
    )

    @Test
    fun `test flyer network response maps all fields`() {
        // Arrange
        val flyer =
            makeFlyer(
                expiresAt = Instant.fromEpochSeconds(2),
                rejectionReason = "Spam",
            )

        // Act
        val networkResponse = flyer.toFlyerNetworkResponse()

        // Assert
        assertEquals(FlyerId("flyer-1"), networkResponse.id)
        assertEquals("Test Flyer", networkResponse.title)
        assertEquals("Test Description", networkResponse.description)
        assertEquals("https://signed.example.com/file.png", networkResponse.fileUrl)
        assertEquals(FlyerStatus.PENDING, networkResponse.status)
        assertEquals(Instant.fromEpochSeconds(2).toString(), networkResponse.expiresAt)
        assertEquals(UserId("user-1"), networkResponse.uploaderId)
        assertEquals(Instant.fromEpochSeconds(0).toString(), networkResponse.createdAt)
        assertEquals(Instant.fromEpochSeconds(1).toString(), networkResponse.updatedAt)
        assertEquals("Spam", networkResponse.rejectionReason)
    }

    @Test
    fun `test flyer network response handles null expiresAt, fileUrl and rejectionReason`() {
        // Arrange
        val flyer = makeFlyer(expiresAt = null, fileUrl = null, rejectionReason = null)

        // Act
        val networkResponse = flyer.toFlyerNetworkResponse()

        // Assert
        assertNull(networkResponse.expiresAt)
        assertNull(networkResponse.fileUrl)
        assertNull(networkResponse.rejectionReason)
    }

    @Test
    fun `test flyer list network response maps pagination and items`() {
        // Arrange
        val page =
            PaginatedList(
                items = listOf(makeFlyer(id = "flyer-1"), makeFlyer(id = "flyer-2")),
                total = 2,
                offset = 0,
                limit = 20,
            )

        // Act
        val networkResponse = page.toFlyerListNetworkResponse()

        // Assert
        assertEquals(listOf(FlyerId("flyer-1"), FlyerId("flyer-2")), networkResponse.flyers.map { it.id })
        assertEquals(2, networkResponse.total)
        assertEquals(0, networkResponse.offset)
        assertEquals(20, networkResponse.limit)
    }

    @Test
    fun `test flyer list network response maps empty page`() {
        // Arrange
        val page = PaginatedList<Flyer>(items = emptyList(), total = 0, offset = 0, limit = 20)

        // Act
        val networkResponse = page.toFlyerListNetworkResponse()

        // Assert
        assertEquals(emptyList(), networkResponse.flyers)
        assertEquals(0, networkResponse.total)
    }

    @Test
    fun `test signed upload network response maps all fields`() {
        // Arrange
        val signedUpload = SignedUpload(signedUrl = "https://signed.example.com/upload", token = "token-123")

        // Act
        val networkResponse = signedUpload.toSignedUploadNetworkResponse()

        // Assert
        assertEquals("https://signed.example.com/upload", networkResponse.signedUrl)
        assertEquals("token-123", networkResponse.token)
    }

    @Test
    fun `test user network response`() {
        // Arrange
        val user =
            User(
                id = UserId("user123"),
                firstName = "John",
                lastName = "Doe",
            )

        // Act
        val networkResponse = user.toUserNetworkResponse(UserRole.USER)

        // Assert
        assertEquals("user123", networkResponse.id)
        assertEquals("John", networkResponse.firstName)
        assertEquals("Doe", networkResponse.lastName)
        assertEquals(UserRole.USER, networkResponse.role)
    }

    @Test
    fun `test user network response with admin role`() {
        // Arrange
        val user =
            User(
                id = UserId("user123"),
                firstName = "John",
                lastName = "Doe",
            )

        // Act
        val networkResponse = user.toUserNetworkResponse(UserRole.ADMIN)

        // Assert
        assertEquals(UserRole.ADMIN, networkResponse.role)
    }
}
