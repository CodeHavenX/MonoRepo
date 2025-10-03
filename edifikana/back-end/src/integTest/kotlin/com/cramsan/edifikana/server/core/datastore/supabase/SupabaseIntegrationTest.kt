package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Invite
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Employee
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.di.FrameworkModule
import com.cramsan.edifikana.server.di.IntegTestApplicationModule
import com.cramsan.edifikana.server.di.SettingsModule
import com.cramsan.edifikana.server.di.SupabaseModule
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.password.generateRandomPassword
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
abstract class SupabaseIntegrationTest : CoroutineTest(), KoinTest {

    protected val supabase: SupabaseClient by inject()

    protected val eventLogDatastore: SupabaseEventLogDatastore by inject()
    protected val propertyDatastore: SupabasePropertyDatastore by inject()
    protected val employeeDatastore: SupabaseEmployeeDatastore by inject()
    protected val timeCardDatastore: SupabaseTimeCardDatastore by inject()
    protected val userDatastore: SupabaseUserDatastore by inject()
    protected val organizationDatastore: SupabaseOrganizationDatastore by inject()

    private val eventLogResources = mutableSetOf<EventLogEntryId>()
    private val propertyResources = mutableSetOf<PropertyId>()
    private val employeeResources = mutableSetOf<EmployeeId>()
    private val timeCardResources = mutableSetOf<TimeCardEventId>()
    private val userResources = mutableSetOf<UserId>()
    private val supabaseUsers = mutableSetOf<String>()
    private val organizationResources = mutableSetOf<OrganizationId>()
    private val invitationResources = mutableSetOf<InviteId>()

    companion object {
        @BeforeAll
        @JvmStatic
        fun classSetup() {
            startKoin {
                modules(
                    FrameworkModule,
                    SettingsModule,
                    IntegTestApplicationModule,
                    SupabaseModule,
                )
            }
        }

        @AfterAll
        @JvmStatic
        fun classTearDown() {
            stopKoin()
        }
    }

    private fun registerEventLogEntryForDeletion(eventLogId: EventLogEntryId) {
        eventLogResources.add(eventLogId)
    }

    private fun registerPropertyForDeletion(propertyId: PropertyId) {
        propertyResources.add(propertyId)
    }

    private fun registerEmployeeForDeletion(employeeId: EmployeeId) {
        employeeResources.add(employeeId)
    }

    private fun registerTimeCardEventForDeletion(timeCardId: TimeCardEventId) {
        timeCardResources.add(timeCardId)
    }

    private fun registerUserForDeletion(userId: UserId) {
        userResources.add(userId)
    }

    private fun registerOrganizationForDeletion(organizationId: OrganizationId) {
        organizationResources.add(organizationId)
    }

    private fun registerInviteForDeletion(inviteId: InviteId) {
        invitationResources.add(inviteId)
    }

    protected fun createTestUser(email: String): UserId {
        val userId = runBlocking {
            userDatastore.createUser(
                email,
                "",
                generateRandomPassword(),
                "test",
                "user",
                false,
            ).getOrThrow().id
        }
        registerUserForDeletion(userId)
        return userId
    }

    protected fun createTestOrganization(): OrganizationId {
        val organizationId = runBlocking {
            organizationDatastore.createOrganization().getOrThrow().id
        }
        registerOrganizationForDeletion(organizationId)
        return organizationId
    }

    protected fun createTestProperty(name: String, userId: UserId, organizationId: OrganizationId): PropertyId {
        val propertyId = runBlocking {
            propertyDatastore.createProperty(
                name, "123 main St", userId, organizationId,
            ).getOrThrow().id
        }
        registerPropertyForDeletion(propertyId)
        return propertyId
    }

    protected fun createTestInvite(
        email: String,
        organizationId: OrganizationId,
        expiration: Instant,
    ): InviteId {
        val inviteId = runBlocking {
            userDatastore.recordInvite(
                email = email,
                organizationId = organizationId,
                expiration = expiration,
            ).getOrThrow().inviteId
        }
        registerInviteForDeletion(inviteId)
        return inviteId
    }

    fun Result<Property>.registerPropertyForDeletion(): Result<Property> {
        return this.onSuccess { property ->
            registerPropertyForDeletion(property.id)
        }
    }

    fun Result<Employee>.registerEmployeeForDeletion(): Result<Employee> {
        return this.onSuccess { employee ->
            registerEmployeeForDeletion(employee.id)
        }
    }

    fun Result<EventLogEntry>.registerEventLogEntryForDeletion(): Result<EventLogEntry> {
        return this.onSuccess { eventLog ->
            registerEventLogEntryForDeletion(eventLog.id)
        }
    }

    fun Result<TimeCardEvent>.registerTimeCardEventForDeletion(): Result<TimeCardEvent> {
        return this.onSuccess { timeCard ->
            registerTimeCardEventForDeletion(timeCard.id)
        }
    }

    fun Result<User>.registerUserForDeletion(): Result<User> {
        return this.onSuccess { user ->
            registerUserForDeletion(user.id)
        }
    }

    fun Result<Organization>.registerOrganizationForDeletion(): Result<Organization> {
        return this.onSuccess { organization ->
            registerOrganizationForDeletion(organization.id)
        }
    }

    fun Result<Invite>.registerInviteForDeletion(): Result<Invite> {
        return this.onSuccess { invite ->
            registerInviteForDeletion(invite.inviteId)
        }
    }

    fun registerSupabaseUserForDeletion(userId: String) {
        supabaseUsers.add(userId)
    }

    protected fun createTestEmployee(
        propertyId: PropertyId,
        firstName: String,
        lastName: String,
    ): EmployeeId {
        val empId = runBlocking {
            employeeDatastore.createEmployee(
                propertyId = propertyId,
                firstName = firstName,
                lastName = lastName,
                role = EmployeeRole.SECURITY, // Use a valid role
                idType = IdType.PASSPORT // Use a valid ID type
            ).getOrThrow().id
        }
        registerEmployeeForDeletion(empId)
        return empId
    }

    @AfterTest
    fun tearDown() {
        // Clean up resources created during tests
        // The order matters since there are foreign key constraints
        runBlocking {
            supabase.auth.signOut()
            supabase.auth.clearSession()

            invitationResources.forEach {
                userDatastore.removeInvite(it).getOrThrow()
            }
            eventLogResources.forEach {
                eventLogDatastore.deleteEventLogEntry(it).getOrThrow()
            }
            timeCardResources.forEach {
                timeCardDatastore.deleteTimeCardEvent(it).getOrThrow()
            }
            employeeResources.forEach {
                employeeDatastore.deleteEmployee(it).getOrThrow()
            }
            propertyResources.forEach {
                propertyDatastore.deleteProperty(it).getOrThrow()
            }
            organizationResources.forEach {
                organizationDatastore.deleteOrganization(it).getOrThrow()
            }
            userResources.forEach {
                userDatastore.deleteUser(it).getOrThrow()
            }
            supabaseUsers.forEach { userId ->
                supabase.auth.admin.deleteUser(userId)
            }
        }
        eventLogResources.clear()
        timeCardResources.clear()
        employeeResources.clear()
        propertyResources.clear()
        userResources.clear()
        organizationResources.clear()
        supabaseUsers.clear()
    }
}
