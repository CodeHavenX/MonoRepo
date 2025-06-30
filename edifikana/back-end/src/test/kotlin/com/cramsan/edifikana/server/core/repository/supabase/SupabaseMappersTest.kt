package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.EnrollmentType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.requests.EnrollUserRequest
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(SupabaseModel::class)
class SupabaseMappersTest {

    @BeforeTest
    fun setUp() {
        AssertUtil.setInstance(NoopAssertUtil())
    }

    @Test
    fun `toUserEntity should map EnrollUserRequest with EMAIL enrollment`() {
        val request = EnrollUserRequest(
            userId = UserId("user-123"),
            enrollmentIdentifier = "test@example.com",
            enrollmentType = EnrollmentType.EMAIL
        )
        val supabaseUserId = "supabase-uid-1"

        val entity = request.toUserEntity(supabaseUserId)

        assertEquals(supabaseUserId, entity.id)
        assertEquals("test@example.com", entity.email)
        assertEquals("", entity.phoneNumber)
        assertEquals("", entity.firstName)
        assertEquals("", entity.lastName)
    }
}
