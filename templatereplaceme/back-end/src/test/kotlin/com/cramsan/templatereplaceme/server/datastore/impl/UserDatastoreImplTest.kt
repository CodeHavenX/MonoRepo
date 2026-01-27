package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.test.CoroutineTest
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserDatastoreImplTest : CoroutineTest() {

    lateinit var userDatastore: UserDatastoreImpl

    @BeforeTest
    fun setUp() {
        userDatastore = UserDatastoreImpl()
    }

    @Test
    fun `test createUser`() = runCoroutineTest {
        // Arrange

        // Act
        val user = userDatastore.createUser(
            firstName = "John",
            lastName = "Doe",
        )

        // Assert
        assertTrue(user.isSuccess)
        val createdUser = user.getOrNull()
        assertNotNull(createdUser)
        assertEquals("John", createdUser?.firstName)
        assertEquals("Doe", createdUser?.lastName)
        assertNotNull(createdUser?.id)
    }
}
