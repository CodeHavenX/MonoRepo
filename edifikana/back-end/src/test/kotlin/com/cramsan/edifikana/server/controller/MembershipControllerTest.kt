package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrgMemberStatus
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.MembershipService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.OrgMemberView
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.time.Instant
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("LargeClass")
class MembershipControllerTest : CoroutineTest(), KoinTest {

    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    /**
     * Clean up the test.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // -------------------------------------------------------------------------
    // inviteMember
    // -------------------------------------------------------------------------

    @Test
    fun `test inviteMember succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/invite_member_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { true }
        coEvery { rbacService.getUserRoleForOrganizationAction(context, orgId) } answers { UserRole.MANAGER }
        coEvery {
            membershipService.inviteMember(orgId, "invite@example.com", InviteRole.EMPLOYEE)
        } answers { Result.success(Unit) }

        // Act
        val response = client.post("membership/invite/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test inviteMember fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/invite_member_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { false }

        // Act
        val response = client.post("membership/invite/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    @Test
    fun `test inviteMember fails when RESIDENT role is requested`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery { contextRetriever.getContext(any()) } answers {
            ClientContext.AuthenticatedClientContext(
                SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
            )
        }

        // Act
        val response = client.post("membership/invite/org123") {
            setBody("""{"email":"invite@example.com","role":"RESIDENT"}""")
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `test inviteMember fails when inviter tries to assign higher privilege role`() = testBackEndApplication {
        // Arrange
        val requestBody = """{"email":"invite@example.com","role":"ADMIN"}"""
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { true }
        // Inviter is MANAGER (level 30); ADMIN invite role maps to level 20 — escalation attempt
        coEvery { rbacService.getUserRoleForOrganizationAction(context, orgId) } answers { UserRole.MANAGER }

        // Act
        val response = client.post("membership/invite/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    // -------------------------------------------------------------------------
    // listMembers
    // -------------------------------------------------------------------------

    @Test
    fun `test listMembers succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/list_members_response.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.EMPLOYEE) } answers { true }
        coEvery { membershipService.listMembers(orgId) } answers {
            Result.success(
                listOf(
                    OrgMemberView(
                        userId = UserId("user123"),
                        orgId = orgId,
                        role = OrgRole.EMPLOYEE,
                        status = OrgMemberStatus.ACTIVE,
                        joinedAt = Instant.fromEpochSeconds(1704067200),
                        email = "member@example.com",
                        firstName = "John",
                        lastName = "Doe",
                    )
                )
            )
        }

        // Act
        val response = client.get("membership/members/org123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test listMembers fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.EMPLOYEE) } answers { false }

        // Act
        val response = client.get("membership/members/org123")

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // updateMemberRole
    // -------------------------------------------------------------------------

    @Test
    fun `test updateMemberRole succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_member_role_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.ADMIN) } answers { true }
        coEvery {
            membershipService.updateMemberRole(orgId, UserId("user123"), OrgRole.ADMIN)
        } answers { Result.success(Unit) }

        // Act
        val response = client.put("membership/members/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test updateMemberRole fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_member_role_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.ADMIN) } answers { false }

        // Act
        val response = client.put("membership/members/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // removeMember
    // -------------------------------------------------------------------------

    @Test
    fun `test removeMember succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/remove_member_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { true }
        coEvery {
            membershipService.removeMember(orgId, UserId("user123"))
        } answers { Result.success(Unit) }

        // Act
        val response = client.delete("membership/members/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test removeMember fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/remove_member_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { false }

        // Act
        val response = client.delete("membership/members/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // leaveOrganization
    // -------------------------------------------------------------------------

    @Test
    fun `test leaveOrganization succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val callerId = UserId("user456")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerId)
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.EMPLOYEE) } answers { true }
        coEvery { membershipService.leaveOrganization(callerId, orgId) } answers { Result.success(Unit) }

        // Act
        val response = client.post("membership/leave/org123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test leaveOrganization fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.EMPLOYEE) } answers { false }

        // Act
        val response = client.post("membership/leave/org123")

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // transferOwnership
    // -------------------------------------------------------------------------

    @Test
    fun `test transferOwnership succeeds when user is the owner`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/transfer_ownership_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val callerId = UserId("user456")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerId)
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRole(context, orgId, UserRole.OWNER) } answers { true }
        coEvery {
            membershipService.transferOwnership(callerId, orgId, UserId("user456"))
        } answers { Result.success(Unit) }

        // Act
        val response = client.post("membership/transfer/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test transferOwnership fails when user is not the owner`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/transfer_ownership_request.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRole(context, orgId, UserRole.OWNER) } answers { false }

        // Act
        val response = client.post("membership/transfer/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // listPendingInvites
    // -------------------------------------------------------------------------

    @Test
    fun `test listPendingInvites succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/list_pending_invites_response.json")
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { true }
        coEvery { membershipService.listPendingInvites(orgId) } answers {
            Result.success(
                listOf(
                    Invite(
                        id = InviteId("invite123"),
                        email = "invite@example.com",
                        organizationId = orgId,
                        role = InviteRole.EMPLOYEE,
                        expiration = Instant.fromEpochSeconds(1705276800),
                        inviteCode = "code123",
                    )
                )
            )
        }

        // Act
        val response = client.get("membership/invites/org123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test listPendingInvites fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { false }

        // Act
        val response = client.get("membership/invites/org123")

        // Assert
        coVerify { membershipService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // cancelInvite
    // -------------------------------------------------------------------------

    @Test
    fun `test cancelInvite succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { membershipService.getInviteOrganization(inviteId) } answers { Result.success(orgId) }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { true }
        coEvery { membershipService.cancelInvite(inviteId) } answers { Result.success(Unit) }

        // Act
        val response = client.delete("membership/invite/invite123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test cancelInvite fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { membershipService.getInviteOrganization(inviteId) } answers { Result.success(orgId) }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { false }

        // Act
        val response = client.delete("membership/invite/invite123")

        // Assert
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // resendInvite
    // -------------------------------------------------------------------------

    @Test
    fun `test resendInvite succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { membershipService.getInviteOrganization(inviteId) } answers { Result.success(orgId) }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { true }
        coEvery { membershipService.resendInvite(inviteId) } answers { Result.success(mockk()) }

        // Act
        val response = client.post("membership/invite/resend/invite123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test resendInvite fails when user does not have required role`() = testBackEndApplication {
        // Arrange
        val membershipService = get<MembershipService>()
        val rbacService = get<RBACService>()
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user456"))
        )
        coEvery { contextRetriever.getContext(any()) } answers { context }
        coEvery { membershipService.getInviteOrganization(inviteId) } answers { Result.success(orgId) }
        coEvery { rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER) } answers { false }

        // Act
        val response = client.post("membership/invite/resend/invite123")

        // Assert
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("You are not authorized to perform this action.", response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // joinViaCode
    // -------------------------------------------------------------------------

    @Test
    fun `test joinViaCode succeeds for authenticated user`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/join_via_code_request.json")
        val membershipService = get<MembershipService>()
        val callerId = UserId("user456")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery { contextRetriever.getContext(any()) } answers {
            ClientContext.AuthenticatedClientContext(
                SupabaseContextPayload(userInfo = mockk(), userId = callerId)
            )
        }
        coEvery { membershipService.joinViaCode(callerId, "ABC123") } answers { Result.success(Unit) }

        // Act
        val response = client.post("membership/join") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
