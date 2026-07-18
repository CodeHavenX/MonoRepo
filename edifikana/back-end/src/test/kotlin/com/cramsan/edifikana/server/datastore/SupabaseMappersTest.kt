package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.common.PhoneNumber
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.datastore.supabase.toUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(kotlin.time.ExperimentalTime::class)
class SupabaseMappersTest {
    @Test
    fun `UserEntity toUser maps all fields correctly`() {
        val entity =
            UserEntity(
                id = UserId("user-123"),
                email = Email("test@example.com"),
                phoneNumber = PhoneNumber("1234567890"),
                firstName = "John",
                lastName = "Doe",
                authMetadata =
                AuthMetadataEntity(
                    pendingAssociation = false,
                    canPasswordAuth = true,
                ),
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
    fun `UserEntity with canPasswordAuth false toUser maps isPasswordSet to false`() {
        val entity =
            UserEntity(
                id = UserId("user-456"),
                email = Email("test2@example.com"),
                phoneNumber = PhoneNumber("0987654321"),
                firstName = "Jane",
                lastName = "Doe",
                authMetadata =
                AuthMetadataEntity(
                    pendingAssociation = true,
                    canPasswordAuth = false,
                ),
            )

        val user = entity.toUser()

        assertEquals(false, user.authMetadata?.isPasswordSet)
    }
}
