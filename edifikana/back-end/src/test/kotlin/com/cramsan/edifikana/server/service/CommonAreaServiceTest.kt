@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.datastore.CommonAreaDatastore
import com.cramsan.edifikana.server.service.models.CommonArea
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test class for [CommonAreaService].
 */
@OptIn(ExperimentalTime::class)
class CommonAreaServiceTest {

    private lateinit var commonAreaDatastore: CommonAreaDatastore
    private lateinit var commonAreaService: CommonAreaService

    /**
     * Sets up the test environment by initializing mocks for [CommonAreaDatastore] and [CommonAreaService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        commonAreaDatastore = mockk()
        commonAreaService = CommonAreaService(commonAreaDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // -------------------------------------------------------------------------
    // createCommonArea
    // -------------------------------------------------------------------------

    /**
     * Tests that createCommonArea delegates to the datastore and returns the created area.
     */
    @Test
    fun `createCommonArea should delegate to datastore and return created area`() = runTest {
        // Arrange
        val propertyId = PropertyId("property123")
        val name = "Main Lobby"
        val type = CommonAreaType.LOBBY
        val description = "Main entrance lobby"
        val commonArea = commonArea(CommonAreaId("area123"), propertyId)
        coEvery {
            commonAreaDatastore.createCommonArea(propertyId, name, type, description)
        } returns Result.success(commonArea)

        // Act
        val result = commonAreaService.createCommonArea(propertyId, name, type, description)

        // Assert
        assertEquals(commonArea, result)
        coVerify { commonAreaDatastore.createCommonArea(propertyId, name, type, description) }
    }

    // -------------------------------------------------------------------------
    // getCommonArea
    // -------------------------------------------------------------------------

    /**
     * Tests that getCommonArea returns the common area when found.
     */
    @Test
    fun `getCommonArea should return area when found`() = runTest {
        // Arrange
        val commonAreaId = CommonAreaId("area123")
        val commonArea = commonArea(commonAreaId, PropertyId("property123"))
        coEvery { commonAreaDatastore.getCommonArea(commonAreaId) } returns Result.success(commonArea)

        // Act
        val result = commonAreaService.getCommonArea(commonAreaId)

        // Assert
        assertEquals(commonArea, result)
    }

    /**
     * Tests that getCommonArea returns null when the area is not found.
     */
    @Test
    fun `getCommonArea should return null when not found`() = runTest {
        // Arrange
        val commonAreaId = CommonAreaId("area123")
        coEvery { commonAreaDatastore.getCommonArea(commonAreaId) } returns Result.success(null)

        // Act
        val result = commonAreaService.getCommonArea(commonAreaId)

        // Assert
        assertNull(result)
    }

    // -------------------------------------------------------------------------
    // getCommonAreasForProperty
    // -------------------------------------------------------------------------

    /**
     * Tests that getCommonAreasForProperty returns all areas for the given property.
     */
    @Test
    fun `getCommonAreasForProperty should return list from datastore`() = runTest {
        // Arrange
        val propertyId = PropertyId("property123")
        val orgId = OrganizationId("org123")
        val areas = listOf(
            commonArea(CommonAreaId("area123"), propertyId),
            commonArea(CommonAreaId("area456"), propertyId),
        )
        coEvery { commonAreaDatastore.getCommonAreasForProperty(propertyId) } returns Result.success(areas)

        // Act
        val result = commonAreaService.getCommonAreasForProperty(propertyId)

        // Assert
        assertEquals(areas, result)
    }

    // -------------------------------------------------------------------------
    // updateCommonArea
    // -------------------------------------------------------------------------

    /**
     * Tests that updateCommonArea delegates to the datastore and returns the updated area.
     */
    @Test
    fun `updateCommonArea should delegate to datastore and return updated area`() = runTest {
        // Arrange
        val commonAreaId = CommonAreaId("area123")
        val name = "Updated Lobby"
        val type = CommonAreaType.LOBBY
        val description: String? = null
        val updatedArea = commonArea(commonAreaId, PropertyId("property123"), name = name)
        coEvery {
            commonAreaDatastore.updateCommonArea(commonAreaId, name, type, description)
        } returns Result.success(updatedArea)

        // Act
        val result = commonAreaService.updateCommonArea(commonAreaId, name, type, description)

        // Assert
        assertEquals(updatedArea, result)
        coVerify { commonAreaDatastore.updateCommonArea(commonAreaId, name, type, description) }
    }

    // -------------------------------------------------------------------------
    // deleteCommonArea
    // -------------------------------------------------------------------------

    /**
     * Tests that deleteCommonArea returns true when the area was successfully soft-deleted.
     */
    @Test
    fun `deleteCommonArea should return true when deleted`() = runTest {
        // Arrange
        val commonAreaId = CommonAreaId("area123")
        coEvery { commonAreaDatastore.deleteCommonArea(commonAreaId) } returns Result.success(true)

        // Act
        val result = commonAreaService.deleteCommonArea(commonAreaId)

        // Assert
        assertTrue(result)
        coVerify { commonAreaDatastore.deleteCommonArea(commonAreaId) }
    }

    /**
     * Tests that deleteCommonArea returns false when the area was not found.
     */
    @Test
    fun `deleteCommonArea should return false when not found`() = runTest {
        // Arrange
        val commonAreaId = CommonAreaId("area123")
        coEvery { commonAreaDatastore.deleteCommonArea(commonAreaId) } returns Result.success(false)

        // Act
        val result = commonAreaService.deleteCommonArea(commonAreaId)

        // Assert
        org.junit.jupiter.api.Assertions.assertFalse(result)
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun commonArea(
        id: CommonAreaId,
        propertyId: PropertyId,
        name: String = "Main Lobby",
        type: CommonAreaType = CommonAreaType.LOBBY,
        description: String? = "Main entrance lobby",
    ) = CommonArea(
        id = id,
        propertyId = propertyId,
        name = name,
        type = type,
        description = description,
        createdAt = Instant.fromEpochMilliseconds(0),
    )
}
