package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
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

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        testUserId = createTestUser("user-${test_prefix}test_prefix@test.com")
    }

    @Test
    fun `createProperty should return property on success`() = runCoroutineTest {
        // Arrange
        val request = CreatePropertyRequest(
            name = "${test_prefix}_Property",
            address = "123 Test St, Test City, TC 12345",
            creatorUserId = testUserId!!,
        )
        // Act
        val result = propertyDatastore.createProperty(request).registerPropertyForDeletion()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(
            Property(
                id = result.getOrNull()!!.id,
                name = request.name,
                address = request.address,
            ),
            result.getOrNull(),
        )

        // Clean up
        propertyDatastore.deleteProperty(DeletePropertyRequest(result.getOrNull()!!.id)).getOrThrow()
    }

    @Test
    fun `getProperty should return created property`() = runCoroutineTest {
        // Arrange
        val createRequest = CreatePropertyRequest(
            name = "${test_prefix}_GetProperty",
            address = "456 Example Ave, Sample Town, ST 67890",
            creatorUserId = testUserId!!,
        )

        // Act
        val createResult = propertyDatastore.createProperty(createRequest).registerPropertyForDeletion()
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val getResult = propertyDatastore.getProperty(GetPropertyRequest(property.id))

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.name == createRequest.name)

        // Clean up
    }

    @Ignore
    @Test
    fun `getProperties should return all properties for user`() = runCoroutineTest {
        // Arrange
        val request1 = CreatePropertyRequest(
            name = "${test_prefix}_PropertyA",
            address = "789 Sample Rd, Example City, EC 10112",
            creatorUserId = testUserId!!,
        )
        val request2 = CreatePropertyRequest(
            name = "${test_prefix}_PropertyB",
            address = "101 Sample Blvd, Testville, TV 13141",
            creatorUserId = testUserId!!,
        )

        // Act
        val result1 = propertyDatastore.createProperty(request1).registerPropertyForDeletion()
        val result2 = propertyDatastore.createProperty(request2).registerPropertyForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = propertyDatastore.getProperties(GetPropertyListsRequest(userId = testUserId!!))

        // Assert
        assertTrue(getAllResult.isSuccess)
        val properties = getAllResult.getOrNull()
        assertNotNull(properties)
        val names = properties!!.map { it.name }
        assertTrue(names.contains(request1.name))
        assertTrue(names.contains(request2.name))
    }

    @Test
    fun `updateProperty should update property fields`() = runCoroutineTest {
        // Arrange
        val createRequest = CreatePropertyRequest(
            name = "${test_prefix}_ToUpdate",
            address = "123 Update St, Update City, UC 12345",
            creatorUserId = testUserId!!,
        )
        val createResult = propertyDatastore.createProperty(createRequest)
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val updateRequest = UpdatePropertyRequest(
            propertyId = property.id,
            name = "${test_prefix}_UpdatedName",
        )

        // Act
        val updateResult = propertyDatastore.updateProperty(updateRequest).registerPropertyForDeletion()

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertTrue(updated.name == updateRequest.name)
    }

    @Test
    fun `deleteProperty should remove property`() = runCoroutineTest {
        // Arrange
        val createRequest = CreatePropertyRequest(
            name = "${test_prefix}_ToDelete",
            address = "789 Delete St, Delete City, DC 67890",
            creatorUserId = testUserId!!,
        )
        val createResult = propertyDatastore.createProperty(createRequest)
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!

        // Act
        val deleteResult = propertyDatastore.deleteProperty(DeletePropertyRequest(property.id))

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = propertyDatastore.getProperty(GetPropertyRequest(property.id))
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteProperty should fail for non-existent property`() = runCoroutineTest {
        // Arrange
        val fakeId = PropertyId("fake-${test_prefix}")

        // Act
        val deleteResult = propertyDatastore.deleteProperty(DeletePropertyRequest(fakeId))

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}
