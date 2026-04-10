package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseRentConfigDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String
    private var propertyId: PropertyId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null
    private var unitId: UnitId? = null

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${testPrefix}@test.com")
            orgId = createTestOrganization("org_$testPrefix", "")
            propertyId = createTestProperty("${testPrefix}_Property", testUserId!!, orgId!!)
            unitId = createTestUnit(propertyId!!, orgId!!, "${testPrefix}_101")
        }
    }

    @Test
    fun `getRentConfig should return null when no config exists`() = runCoroutineTest {
        // Arrange — unitId has no rent config yet

        // Act
        val result = rentConfigDatastore.getRentConfig(unitId!!)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `setRentConfig should insert a new config when none exists`() = runCoroutineTest {
        // Arrange
        val monthlyAmount = 150000L
        val dueDay = 5
        val currency = "USD"

        // Act
        val result = rentConfigDatastore.setRentConfig(
            unitId = unitId!!,
            monthlyAmount = monthlyAmount,
            dueDay = dueDay,
            currency = currency,
            updatedBy = testUserId,
        ).registerRentConfigForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val config = result.getOrNull()
        assertNotNull(config)
        assertEquals(unitId, config.unitId)
        assertEquals(monthlyAmount, config.monthlyAmount)
        assertEquals(dueDay, config.dueDay)
        assertEquals(currency, config.currency)
    }

    @Test
    fun `setRentConfig should update existing config when called again`() = runCoroutineTest {
        // Arrange
        val initial = rentConfigDatastore.setRentConfig(
            unitId = unitId!!,
            monthlyAmount = 100000L,
            dueDay = 1,
            currency = "USD",
            updatedBy = testUserId,
        ).registerRentConfigForDeletion()
        assertTrue(initial.isSuccess)
        val originalId = initial.getOrNull()!!.id

        // Act
        val updated = rentConfigDatastore.setRentConfig(
            unitId = unitId!!,
            monthlyAmount = 120000L,
            dueDay = 10,
            currency = "USD",
            updatedBy = testUserId,
        )

        // Assert
        assertTrue(updated.isSuccess)
        val config = updated.getOrNull()
        assertNotNull(config)
        assertEquals(originalId, config.id)
        assertEquals(120000L, config.monthlyAmount)
        assertEquals(10, config.dueDay)
    }

    @Test
    fun `getRentConfig should return config after insert`() = runCoroutineTest {
        // Arrange
        val setResult = rentConfigDatastore.setRentConfig(
            unitId = unitId!!,
            monthlyAmount = 200000L,
            dueDay = 15,
            currency = "PEN",
            updatedBy = testUserId,
        ).registerRentConfigForDeletion()
        assertTrue(setResult.isSuccess)

        // Act
        val getResult = rentConfigDatastore.getRentConfig(unitId!!)

        // Assert
        assertTrue(getResult.isSuccess)
        val config = getResult.getOrNull()
        assertNotNull(config)
        assertEquals(unitId, config.unitId)
        assertEquals(200000L, config.monthlyAmount)
        assertEquals("PEN", config.currency)
    }

    @Test
    fun `deleteRentConfigByUnitId should soft delete and make config invisible`() = runCoroutineTest {
        // Arrange
        val setResult = rentConfigDatastore.setRentConfig(
            unitId = unitId!!,
            monthlyAmount = 100000L,
            dueDay = 1,
            currency = "USD",
            updatedBy = testUserId,
        ).registerRentConfigForDeletion()
        assertTrue(setResult.isSuccess)

        // Act
        val deleteResult = rentConfigDatastore.deleteRentConfigByUnitId(unitId!!)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = rentConfigDatastore.getRentConfig(unitId!!)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `purgeRentConfig should hard delete the config`() = runCoroutineTest {
        // Arrange
        val setResult = rentConfigDatastore.setRentConfig(
            unitId = unitId!!,
            monthlyAmount = 100000L,
            dueDay = 1,
            currency = "USD",
            updatedBy = testUserId,
        )
        assertTrue(setResult.isSuccess)
        val config = setResult.getOrNull()!!

        // Act
        val purgeResult = rentConfigDatastore.purgeRentConfig(config.id)

        // Assert
        assertTrue(purgeResult.isSuccess)
        val getResult = rentConfigDatastore.getRentConfig(unitId!!)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }
}
