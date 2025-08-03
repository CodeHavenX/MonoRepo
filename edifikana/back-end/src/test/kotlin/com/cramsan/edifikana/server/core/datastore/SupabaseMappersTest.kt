package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseModel
import com.cramsan.edifikana.server.core.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.core.datastore.supabase.toUser
import com.cramsan.edifikana.server.core.datastore.supabase.toUserEntity
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(SupabaseModel::class)
class SupabaseMappersTest {

    @Test
    fun `UserEntity toUser maps all fields correctly`() {
        val entity = UserEntity(
            id = "user-123",
            email = "test@example.com",
            phoneNumber = "1234567890",
            firstName = "John",
            lastName = "Doe",
            authMetadata = AuthMetadataEntity(
                pendingAssociation = false,
                canPasswordAuth = true
            )
        )

        val user = entity.toUser()

        assertEquals(UserId("user-123"), user.id)
        assertEquals("test@example.com", user.email)
        assertEquals("1234567890", user.phoneNumber)
        assertEquals("John", user.firstName)
        assertEquals("Doe", user.lastName)
        assertEquals(true, user.authMetadata?.isPasswordSet)
    }

    @Test
    fun `CreateUserRequest toUserEntity maps all fields correctly`() {
        val request = CreateUserRequest(
            email = "test@example.com",
            phoneNumber = "1234567890",
            firstName = "Jane",
            lastName = "Smith",
            password = "password123"
        )
        val userId = UserId("user-456")
        val pendingAssociation = true
        val canPasswordAuth = false

        val entity = request.toUserEntity(userId, pendingAssociation, canPasswordAuth)

        assertEquals("user-456", entity.id)
        assertEquals("test@example.com", entity.email)
        assertEquals("1234567890", entity.phoneNumber)
        assertEquals("Jane", entity.firstName)
        assertEquals("Smith", entity.lastName)
        assertEquals(pendingAssociation, entity.authMetadata.pendingAssociation)
        assertEquals(canPasswordAuth, entity.authMetadata.canPasswordAuth)
    }
}
