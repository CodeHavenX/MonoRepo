package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.server.datastore.UnitDatastore
import com.cramsan.edifikana.server.service.models.Unit
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest

/**
 * Test class for [UnitService].
 */
class UnitServiceTest {
    private lateinit var unitDatastore: UnitDatastore
    private lateinit var unitService: UnitService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        unitDatastore = mockk()
        unitService = UnitService(unitDatastore)
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createUnit calls the datastore with all parameters and returns the result.
     */
    @Test
    fun `createUnit should call unitDatastore and return unit`() = runTest {
        // Arrange
        val unit = mockk<Unit>()
        coEvery {
            unitDatastore.createUnit(any(), any(), any(), any(), any(), any(), any())
        } returns Result.success(unit)

        // Act
        val result = unitService.createUnit(
            propertyId = PropertyId("prop1"),
            unitNumber = "101",
            bedrooms = 2,
            bathrooms = 1,
            sqFt = 750,
            floor = 1,
            notes = null,
        )

        // Assert
        assertEquals(unit, result)
        coVerify {
            unitDatastore.createUnit(
                propertyId = PropertyId("prop1"),
                unitNumber = "101",
                bedrooms = 2,
                bathrooms = 1,
                sqFt = 750,
                floor = 1,
                notes = null,
            )
        }
    }

    /**
     * Tests that getUnit returns the unit when the datastore finds it.
     */
    @Test
    fun `getUnit should call unitDatastore and return unit when found`() = runTest {
        // Arrange
        val unitId = UnitId("unit1")
        val unit = mockk<Unit>()
        coEvery { unitDatastore.getUnit(unitId) } returns Result.success(unit)

        // Act
        val result = unitService.getUnit(unitId)

        // Assert
        assertEquals(unit, result)
        coVerify { unitDatastore.getUnit(unitId) }
    }

    /**
     * Tests that getUnit returns null when the datastore returns a null result.
     */
    @Test
    fun `getUnit should return null when unit does not exist`() = runTest {
        // Arrange
        val unitId = UnitId("missing")
        coEvery { unitDatastore.getUnit(unitId) } returns Result.success(null)

        // Act
        val result = unitService.getUnit(unitId)

        // Assert
        assertNull(result)
        coVerify { unitDatastore.getUnit(unitId) }
    }

    /**
     * Tests that getUnit returns null when the datastore returns a failure.
     */
    @Test
    fun `getUnit should return null when datastore fails`() = runTest {
        // Arrange
        val unitId = UnitId("missing")
        coEvery { unitDatastore.getUnit(unitId) } returns Result.failure(Exception("Not found"))

        // Act
        val result = unitService.getUnit(unitId)

        // Assert
        assertNull(result)
        coVerify { unitDatastore.getUnit(unitId) }
    }

    /**
     * Tests that getUnits returns the list of units for an org, filtered by property.
     */
    @Test
    fun `getUnits should call unitDatastore with propertyId filter and return list`() = runTest {
        // Arrange
        val propertyId = PropertyId("prop1")
        val units = listOf(mockk<Unit>(), mockk<Unit>())
        coEvery { unitDatastore.getUnits(propertyId) } returns Result.success(units)

        // Act
        val result = unitService.getUnits(propertyId)

        // Assert
        assertEquals(units, result)
        coVerify { unitDatastore.getUnits(propertyId) }
    }

    /**
     * Tests that getUnits returns all org units when propertyId is null.
     */
    @Test
    fun `getUnits should call unitDatastore without property filter when null`() = runTest {
        // Arrange
        val orgId = OrganizationId("org1")
        val units = listOf(mockk<Unit>())
        coEvery { unitDatastore.getUnits(orgId) } returns Result.success(units)

        // Act
        val result = unitService.getUnits(orgId)

        // Assert
        assertEquals(units, result)
        coVerify { unitDatastore.getUnits(orgId) }
    }

    /**
     * Tests that updateUnit calls the datastore with all provided parameters and returns the result.
     */
    @Test
    fun `updateUnit should call unitDatastore and return updated unit`() = runTest {
        // Arrange
        val unitId = UnitId("unit1")
        val unit = mockk<Unit>()
        coEvery {
            unitDatastore.updateUnit(any(), any(), any(), any(), any(), any(), any())
        } returns Result.success(unit)

        // Act
        val result = unitService.updateUnit(
            unitId = unitId,
            unitNumber = "101B",
            bedrooms = 3,
            bathrooms = 2,
            sqFt = 900,
            floor = 1,
            notes = "Renovated",
        )

        // Assert
        assertEquals(unit, result)
        coVerify {
            unitDatastore.updateUnit(
                unitId = unitId,
                unitNumber = "101B",
                bedrooms = 3,
                bathrooms = 2,
                sqFt = 900,
                floor = 1,
                notes = "Renovated",
            )
        }
    }

    /**
     * Tests that null fields in updateUnit are forwarded to the datastore as-is (no change semantics).
     */
    @Test
    fun `updateUnit should forward null fields to datastore unchanged`() = runTest {
        // Arrange
        val unitId = UnitId("unit1")
        val unit = mockk<Unit>()
        coEvery {
            unitDatastore.updateUnit(any(), any(), any(), any(), any(), any(), any())
        } returns Result.success(unit)

        // Act
        val result = unitService.updateUnit(
            unitId = unitId,
            unitNumber = null,
            bedrooms = null,
            bathrooms = null,
            sqFt = null,
            floor = null,
            notes = null,
        )

        // Assert
        assertEquals(unit, result)
        coVerify {
            unitDatastore.updateUnit(
                unitId = unitId,
                unitNumber = null,
                bedrooms = null,
                bathrooms = null,
                sqFt = null,
                floor = null,
                notes = null,
            )
        }
    }

    /**
     * Tests that deleteUnit calls the datastore and returns the success result.
     */
    @Test
    fun `deleteUnit should call unitDatastore and return true`() = runTest {
        // Arrange
        val unitId = UnitId("unit1")
        coEvery { unitDatastore.deleteUnit(unitId) } returns Result.success(true)

        // Act
        val result = unitService.deleteUnit(unitId)

        // Assert
        assertEquals(true, result)
        coVerify { unitDatastore.deleteUnit(unitId) }
    }
}
