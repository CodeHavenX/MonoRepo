package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
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
import kotlin.test.assertTrue

class SupabaseUserDatabaseIntegrationTest : TestBase(), KoinTest {

    private val database: SupabaseUserDatabase by inject()
    private lateinit var test_prefix: String

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
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
    fun `createUser should return user on success`() = runBlockingTest {
        // Arrange
        val request = CreateUserRequest(
            email = "${test_prefix}_user@test.com",
            phoneNumber = "123-456-7890",
            password = "",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
        )

        // Act
        val result = database.createUser(request)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `createUser should fail with existing email`() = runBlockingTest {
        val request = CreateUserRequest(
            email = "${test_prefix}_dupe@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
        )
        val first = database.createUser(request)
        assertTrue(first.isSuccess)
        val second = database.createUser(request)
        assertTrue(second.isFailure)
    }

    @Test
    fun `createUser should fail with invalid email`() = runBlockingTest {
        val request = CreateUserRequest(
            email = "not-an-email",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Invalid",
            lastName = "User",
        )
        val result = database.createUser(request)
        assertTrue(result.isFailure)
    }

    @Test
    fun `getUser should return created user`() = runBlockingTest {
        val request = CreateUserRequest(
            email = "${test_prefix}_getuser@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Get",
            lastName = "User",
        )
        val createResult = database.createUser(request)
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val getResult = database.getUser(GetUserRequest(user.id))
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertTrue(fetched != null && fetched.email == request.email)
    }

    @Test
    fun `deleteUser should remove user`() = runBlockingTest {
        val request = CreateUserRequest(
            email = "${test_prefix}_delete@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Delete",
            lastName = "User",
        )
        val createResult = database.createUser(request)
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val deleteResult = database.deleteUser(DeleteUserRequest(user.id))
        assertTrue(deleteResult.isSuccess && deleteResult.getOrNull() == true)
        val getResult = database.getUser(GetUserRequest(user.id))
        assertTrue(getResult.isSuccess && getResult.getOrNull() == null)
    }

    @Test
    fun `deleteUser should fail for non-existent user`() = runBlockingTest {
        val fakeId = com.cramsan.edifikana.lib.model.UserId("fake-${test_prefix}")
        val deleteResult = database.deleteUser(DeleteUserRequest(fakeId))
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}