package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.integTestFrameworkModule
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.edifikana.server.dependencyinjection.DatastoreModule
import com.cramsan.edifikana.server.dependencyinjection.IntegTestApplicationModule
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.password.generateRandomPassword
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@OptIn(ExperimentalTime::class)
abstract class SupabaseIntegrationTest : CoroutineTest(), KoinTest {

    protected val supabase: SupabaseClient by inject()

    protected val eventLogDatastore: SupabaseEventLogDatastore by inject()
    protected val propertyDatastore: SupabasePropertyDatastore by inject()
    protected val employeeDatastore: SupabaseEmployeeDatastore by inject()
    protected val timeCardDatastore: SupabaseTimeCardDatastore by inject()
    protected val userDatastore: SupabaseUserDatastore by inject()
    protected val organizationDatastore: SupabaseOrganizationDatastore by inject()
    protected val notificationDatastore: SupabaseNotificationDatastore by inject()

    private val eventLogResources = mutableSetOf<EventLogEntryId>()
    private val propertyResources = mutableSetOf<PropertyId>()
    private val employeeResources = mutableSetOf<EmployeeId>()
    private val timeCardResources = mutableSetOf<TimeCardEventId>()
    private val userResources = mutableSetOf<UserId>()
    private val supabaseUsers = mutableSetOf<String>()
    private val organizationResources = mutableSetOf<OrganizationId>()
    private val invitationResources = mutableSetOf<InviteId>()
    private val notificationResources = mutableSetOf<NotificationId>()

    @BeforeEach
    fun supabaseSetup() {
        startKoin {
            allowOverride(true)
            modules(
                TestArchitectureModule,
                integTestFrameworkModule("EDIFIKANA"),
                DatastoreModule,
                IntegTestApplicationModule, // Override Clock with TestTimeSource clock
            )
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

    private fun registerNotificationForDeletion(notificationId: NotificationId) {
        notificationResources.add(notificationId)
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

    protected fun createTestOrganization(
        name: String,
        description: String,
    ): OrganizationId {
        val organizationId = runBlocking {
            organizationDatastore.createOrganization(
                name = name,
                description = description,
            ).getOrThrow().id
        }
        registerOrganizationForDeletion(organizationId)
        return organizationId
    }

    protected fun createTestProperty(name: String, userId: UserId, organizationId: OrganizationId): PropertyId {
        val propertyId = runBlocking {
            propertyDatastore.createProperty(
                name,
                "123 main St",
                userId,
                organizationId,
            ).getOrThrow().id
        }
        registerPropertyForDeletion(propertyId)
        return propertyId
    }

    protected fun createTestInvite(
        email: String,
        organizationId: OrganizationId,
        expiration: Instant,
        role: UserRole = UserRole.USER,
    ): InviteId {
        val inviteId = runBlocking {
            userDatastore.recordInvite(
                email = email,
                organizationId = organizationId,
                expiration = expiration,
                role = role,
            ).getOrThrow().id
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
            registerInviteForDeletion(invite.id)
        }
    }

    fun Result<Notification>.registerNotificationForDeletion(): Result<Notification> {
        return this.onSuccess { notification ->
            registerNotificationForDeletion(notification.id)
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
        // First soft delete, then purge each record

        val results = mutableListOf<Result<*>>()
        try {
            runBlocking {
                supabase.auth.signOut()
                supabase.auth.clearSession()

                notificationResources.forEach {
                    results += notificationDatastore.deleteNotification(it)
                    results += notificationDatastore.purgeNotification(it)
                }
                invitationResources.forEach {
                    results += userDatastore.removeInvite(it)
                    results += userDatastore.purgeInvite(it)
                }
                eventLogResources.forEach {
                    results += eventLogDatastore.deleteEventLogEntry(it)
                    results += eventLogDatastore.purgeEventLogEntry(it)
                }
                timeCardResources.forEach {
                    results += timeCardDatastore.deleteTimeCardEvent(it)
                    results += timeCardDatastore.purgeTimeCardEvent(it)
                }
                employeeResources.forEach {
                    results += employeeDatastore.deleteEmployee(it)
                    results += employeeDatastore.purgeEmployee(it)
                }
                propertyResources.forEach {
                    results += propertyDatastore.deleteProperty(it)
                    results += propertyDatastore.purgeProperty(it)
                }
                organizationResources.forEach {
                    results += organizationDatastore.deleteOrganization(it)
                    results += organizationDatastore.purgeOrganization(it)
                }
                userResources.forEach {
                    results += userDatastore.deleteUser(it)
                    results += userDatastore.purgeUser(it)
                }
                supabaseUsers.forEach { userId ->
                    supabase.auth.admin.deleteUser(userId)
                }
            }
        } finally {
            notificationResources.clear()
            invitationResources.clear()
            eventLogResources.clear()
            timeCardResources.clear()
            employeeResources.clear()
            propertyResources.clear()
            userResources.clear()
            organizationResources.clear()
            supabaseUsers.clear()
            stopKoin()
        }

        results.forEach { it.requireSuccess() }
    }
}
