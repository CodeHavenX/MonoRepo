package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
import com.cramsan.edifikana.server.di.FrameworkModule
import com.cramsan.edifikana.server.di.IntegTestApplicationModule
import com.cramsan.edifikana.server.di.SettingsModule
import com.cramsan.edifikana.server.di.SupabaseModule
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.utils.uuid.UUID
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabasePropertyDatabaseIntegrationTest : TestBase(), KoinTest {

    private val database: SupabasePropertyDatabase by inject()
    private lateinit var test_prefix: String
    private var testUserId: UserId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        testUserId = UserId("user-${'$'}test_prefix")
        startKoin {
            modules(
                FrameworkModule,
                SettingsModule,
                IntegTestApplicationModule,
                SupabaseModule,
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `createProperty should return property on success`() = runBlockingTest {
        val request = CreatePropertyRequest(
            name = "${'$'}{test_prefix}_Property",
        )
        val result = database.createProperty(request)
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getProperty should return created property`() = runBlockingTest {
        val createRequest = CreatePropertyRequest(
            name = "${'$'}{test_prefix}_GetProperty",
        )
        val createResult = database.createProperty(createRequest)
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val getResult = database.getProperty(GetPropertyRequest(property.id))
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.name == createRequest.name)
    }

    @Test
    fun `getProperties should return all properties for user`() = runBlockingTest {
        val request1 = CreatePropertyRequest(
            name = "${'$'}{test_prefix}_PropertyA",
        )
        val request2 = CreatePropertyRequest(
            name = "${'$'}{test_prefix}_PropertyB",
        )
        val result1 = database.createProperty(request1)
        val result2 = database.createProperty(request2)
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = database.getProperties(GetPropertyListsRequest(userId = testUserId!!, showAll = true))
        assertTrue(getAllResult.isSuccess)
        val properties = getAllResult.getOrNull()
        assertNotNull(properties)
        val names = properties!!.map { it.name }
        assertTrue(names.contains(request1.name))
        assertTrue(names.contains(request2.name))
    }

    @Test
    fun `updateProperty should update property fields`() = runBlockingTest {
        val createRequest = CreatePropertyRequest(
            name = "${'$'}{test_prefix}_ToUpdate",
        )
        val createResult = database.createProperty(createRequest)
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val updateRequest = UpdatePropertyRequest(
            propertyId = property.id,
            name = "${'$'}{test_prefix}_UpdatedName",
        )
        val updateResult = database.updateProperty(updateRequest)
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertTrue(updated.name == updateRequest.name)
    }

    @Test
    fun `deleteProperty should remove property`() = runBlockingTest {
        val createRequest = CreatePropertyRequest(
            name = "${'$'}{test_prefix}_ToDelete",
        )
        val createResult = database.createProperty(createRequest)
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val deleteResult = database.deleteProperty(DeletePropertyRequest(property.id))
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = database.getProperty(GetPropertyRequest(property.id))
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteProperty should fail for non-existent property`() = runBlockingTest {
        val fakeId = PropertyId("fake-${'$'}test_prefix")
        val deleteResult = database.deleteProperty(DeletePropertyRequest(fakeId))
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}

