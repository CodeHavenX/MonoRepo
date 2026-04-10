@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.RentConfigDatastore
import com.cramsan.edifikana.server.service.models.RentConfig
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
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test class for [RentConfigService].
 */
@OptIn(ExperimentalTime::class)
class RentConfigServiceTest {

    private lateinit var rentConfigDatastore: RentConfigDatastore
    private lateinit var rentConfigService: RentConfigService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        rentConfigDatastore = mockk()
        rentConfigService = RentConfigService(rentConfigDatastore)
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // -------------------------------------------------------------------------
    // getRentConfig
    // -------------------------------------------------------------------------

    /**
     * Tests that getRentConfig returns the config when found.
     */
    @Test
    fun `getRentConfig should return config when found`() = runTest {
        // Arrange
        val unitId = UnitId("unit123")
        val config = rentConfig(RentConfigId("rc123"), unitId)
        coEvery { rentConfigDatastore.getRentConfig(unitId) } returns Result.success(config)

        // Act
        val result = rentConfigService.getRentConfig(unitId)

        // Assert
        assertEquals(config, result)
    }

    /**
     * Tests that getRentConfig returns null when no config exists for the unit.
     */
    @Test
    fun `getRentConfig should return null when not found`() = runTest {
        // Arrange
        val unitId = UnitId("unit123")
        coEvery { rentConfigDatastore.getRentConfig(unitId) } returns Result.success(null)

        // Act
        val result = rentConfigService.getRentConfig(unitId)

        // Assert
        assertNull(result)
    }

    // -------------------------------------------------------------------------
    // setRentConfig
    // -------------------------------------------------------------------------

    /**
     * Tests that setRentConfig delegates to the datastore and returns the saved config.
     */
    @Test
    fun `setRentConfig should delegate to datastore and return saved config`() = runTest {
        // Arrange
        val unitId = UnitId("unit123")
        val updatedBy = UserId("user123")
        val config = rentConfig(RentConfigId("rc123"), unitId)
        coEvery {
            rentConfigDatastore.setRentConfig(
                unitId = unitId,
                monthlyAmount = 120000L,
                dueDay = 1,
                currency = "USD",
                updatedBy = updatedBy,
            )
        } returns Result.success(config)

        // Act
        val result = rentConfigService.setRentConfig(
            unitId = unitId,
            monthlyAmount = 120000L,
            dueDay = 1,
            currency = "USD",
            updatedBy = updatedBy,
        )

        // Assert
        assertEquals(config, result)
        coVerify {
            rentConfigDatastore.setRentConfig(
                unitId = unitId,
                monthlyAmount = 120000L,
                dueDay = 1,
                currency = "USD",
                updatedBy = updatedBy,
            )
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun rentConfig(
        id: RentConfigId,
        unitId: UnitId,
    ) = RentConfig(
        id = id,
        unitId = unitId,
        monthlyAmount = 120000L,
        dueDay = 1,
        currency = "USD",
        updatedAt = Instant.fromEpochMilliseconds(0),
        updatedBy = UserId("user123"),
        createdAt = Instant.fromEpochMilliseconds(0),
    )
}
