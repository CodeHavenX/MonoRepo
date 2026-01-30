package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.framework.utils.uuid.UUID
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabasePropertyDatastoreIntegrationTest : SupabaseIntegrationTest() {
    private lateinit var test_prefix: String
    private var testUserId: UserId? = null
    private var testOrg: OrganizationId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        testUserId = createTestUser("user-${test_prefix}@test.com")
        testOrg = createTestOrganization("org-${test_prefix}","")
    }

    @Test
    fun `createProperty should return property on success`() = runCoroutineTest {
        // Arrange

        // Act
        val result = propertyDatastore.createProperty(
            name = "${test_prefix}_Property",
            address = "123 Test St, Test City, TC 12345",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:CASA",
        ).registerPropertyForDeletion()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(
            Property(
                id = result.getOrNull()!!.id,
                name = "${test_prefix}_Property",
                address = "123 Test St, Test City, TC 12345",
                organizationId = testOrg!!,
                imageUrl = "drawable:CASA",
            ),
            result.getOrNull(),
        )

        // Clean up
        propertyDatastore.deleteProperty(result.getOrNull()!!.id).getOrThrow()
    }

    @Test
    fun `getProperty should return created property`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = propertyDatastore.createProperty(
            name = "${test_prefix}_GetProperty",
            address = "456 Example Ave, Sample Town, ST 67890",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:QUINTA",
        ).registerPropertyForDeletion()
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val getResult = propertyDatastore.getProperty(property.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.name == "${test_prefix}_GetProperty")
        assertEquals("drawable:QUINTA", fetched.imageUrl)

        // Clean up
    }

    @Ignore
    @Test
    fun `getProperties should return all properties for user`() = runCoroutineTest {
        // Arrange

        // Act
        val result1 = propertyDatastore.createProperty(
            name = "${test_prefix}_PropertyA",
            address = "789 Sample Rd, Example City, EC 10112",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:L_DEPA",
        ).registerPropertyForDeletion()
        val result2 = propertyDatastore.createProperty(
            name = "${test_prefix}_PropertyB",
            address = "101 Sample Blvd, Testville, TV 13141",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:M_DEPA",
        ).registerPropertyForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = propertyDatastore.getProperties(userId = testUserId!!)

        // Assert
        assertTrue(getAllResult.isSuccess)
        val properties = getAllResult.getOrNull()
        assertNotNull(properties)
        val names = properties!!.map { it.name }
        assertTrue(names.contains( "${test_prefix}_PropertyA"))
        assertTrue(names.contains( "${test_prefix}_PropertyB"))
    }

    @Test
    fun `updateProperty should update only name when address is null`() = runCoroutineTest {
        // Arrange
        val createResult = propertyDatastore.createProperty(
            name = "${test_prefix}_ToUpdate",
            address = "123 Update St, Update City, UC 12345",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:CASA",
        ).registerPropertyForDeletion()
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!

        // Act
        val updateResult = propertyDatastore.updateProperty(
            propertyId = property.id,
            name = "${test_prefix}_UpdatedName",
            address = null,
            imageUrl = "drawable:S_DEPA",
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals("${test_prefix}_UpdatedName", updated.name)
        assertEquals("123 Update St, Update City, UC 12345", updated.address)
        assertEquals("drawable:S_DEPA", updated.imageUrl)
    }

    @Test
    fun `updateProperty should update only address when name is null`() = runCoroutineTest {
        // Arrange
        val createResult = propertyDatastore.createProperty(
            name = "${test_prefix}_ToUpdate",
            address = "123 Update St, Update City, UC 12345",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:QUINTA",
        ).registerPropertyForDeletion()
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!

        // Act
        val updateResult = propertyDatastore.updateProperty(
            propertyId = property.id,
            name = null,
            address = "456 New Address St, New City, NC 67890",
            imageUrl = "drawable:M_DEPA",
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals("${test_prefix}_ToUpdate", updated.name)
        assertEquals("456 New Address St, New City, NC 67890", updated.address)
        assertEquals("drawable:M_DEPA", updated.imageUrl)
    }

    @Test
    fun `updateProperty should update both name and address`() = runCoroutineTest {
        // Arrange
        val createResult = propertyDatastore.createProperty(
            name = "${test_prefix}_ToUpdate",
            address = "123 Update St, Update City, UC 12345",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:L_DEPA",
        ).registerPropertyForDeletion()
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!

        // Act
        val updateResult = propertyDatastore.updateProperty(
            propertyId = property.id,
            name = "${test_prefix}_CompletelyUpdated",
            address = "789 Complete St, Full City, FC 11111",
            imageUrl = "drawable:CASA",
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals("${test_prefix}_CompletelyUpdated", updated.name)
        assertEquals("789 Complete St, Full City, FC 11111", updated.address)
        assertEquals("drawable:CASA", updated.imageUrl)
    }

    @Test
    fun `deleteProperty should remove property`() = runCoroutineTest {
        // Arrange
        val createResult = propertyDatastore.createProperty(
            name = "${test_prefix}_ToDelete",
            address = "789 Delete St, Delete City, DC 67890",
            creatorUserId = testUserId!!,
            organizationId = testOrg!!,
            imageUrl = "drawable:QUINTA",
        )
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!

        // Act
        val deleteResult = propertyDatastore.deleteProperty(property.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = propertyDatastore.getProperty(property.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())

        // Clean up
        propertyDatastore.purgeProperty(property.id)
    }

    @Test
    fun `deleteProperty should fail for non-existent property`() = runCoroutineTest {
        // Arrange
        val fakeId = PropertyId("fake-${test_prefix}")

        // Act
        val deleteResult = propertyDatastore.deleteProperty(fakeId)

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}
