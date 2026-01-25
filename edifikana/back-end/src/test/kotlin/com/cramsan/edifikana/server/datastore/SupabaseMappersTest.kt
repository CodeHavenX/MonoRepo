package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.datastore.supabase.toUser
import com.cramsan.framework.annotations.SupabaseModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(SupabaseModel::class, kotlin.time.ExperimentalTime::class)
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
}
