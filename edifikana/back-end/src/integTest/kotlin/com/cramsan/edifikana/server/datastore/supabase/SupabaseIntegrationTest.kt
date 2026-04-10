package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.integTestFrameworkModule
import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.employee.EmployeeRole
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.identification.IdType
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.task.TaskId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.edifikana.server.dependencyinjection.DatastoreModule
import com.cramsan.edifikana.server.dependencyinjection.IntegTestApplicationModule
import com.cramsan.edifikana.server.service.models.CommonArea
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.PaymentRecord
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.edifikana.server.service.models.RentConfig
import com.cramsan.edifikana.server.service.models.Task
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import com.cramsan.edifikana.server.service.models.Unit
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.password.generateRandomPassword
import com.cramsan.framework.utils.uuid.UUID
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
    protected val membershipDatastore: SupabaseMembershipDatastore by inject()
    protected val commonAreaDatastore: SupabaseCommonAreaDatastore by inject()
    protected val taskDatastore: SupabaseTaskDatastore by inject()
    protected val unitDatastore: SupabaseUnitDatastore by inject()
    protected val paymentRecordDatastore: SupabasePaymentRecordDatastore by inject()
    protected val rentConfigDatastore: SupabaseRentConfigDatastore by inject()

    private val eventLogResources = mutableSetOf<EventLogEntryId>()
    private val commonAreaResources = mutableSetOf<CommonAreaId>()
    private val taskResources = mutableSetOf<TaskId>()
    private val propertyResources = mutableSetOf<PropertyId>()
    private val employeeResources = mutableSetOf<EmployeeId>()
    private val timeCardResources = mutableSetOf<TimeCardEventId>()
    private val userResources = mutableSetOf<UserId>()
    private val supabaseUsers = mutableSetOf<String>()
    private val organizationResources = mutableSetOf<OrganizationId>()
    private val invitationResources = mutableSetOf<InviteId>()
    private val notificationResources = mutableSetOf<NotificationId>()
    private val unitResources = mutableSetOf<UnitId>()
    private val paymentRecordResources = mutableSetOf<PaymentRecordId>()
    private val rentConfigResources = mutableSetOf<RentConfigId>()

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

    private fun registerCommonAreaForDeletion(commonAreaId: CommonAreaId) {
        commonAreaResources.add(commonAreaId)
    }

    private fun registerTaskForDeletion(taskId: TaskId) {
        taskResources.add(taskId)
    }

    private fun registerUnitForDeletion(unitId: UnitId) {
        unitResources.add(unitId)
    }

    private fun registerPaymentRecordForDeletion(paymentRecordId: PaymentRecordId) {
        paymentRecordResources.add(paymentRecordId)
    }

    private fun registerRentConfigForDeletion(rentConfigId: RentConfigId) {
        rentConfigResources.add(rentConfigId)
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

    protected fun createTestUnit(
        propertyId: PropertyId,
        unitNumber: String,
    ): UnitId {
        val unitId = runBlocking {
            unitDatastore.createUnit(
                propertyId = propertyId,
                unitNumber = unitNumber,
                bedrooms = null,
                bathrooms = null,
                sqFt = null,
                floor = null,
                notes = null,
            ).getOrThrow().id
        }
        registerUnitForDeletion(unitId)
        return unitId
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
        role: InviteRole = InviteRole.EMPLOYEE,
    ): InviteId {
        val inviteId = runBlocking {
            membershipDatastore.createInvite(
                email = email,
                organizationId = organizationId,
                expiration = expiration,
                role = role,
                inviteCode = UUID.random().replace("-", "").take(12).uppercase(),
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

    fun Result<CommonArea>.registerCommonAreaForDeletion(): Result<CommonArea> {
        return this.onSuccess { commonArea ->
            registerCommonAreaForDeletion(commonArea.id)
        }
    }

    fun Result<Task>.registerTaskForDeletion(): Result<Task> {
        return this.onSuccess { task ->
            registerTaskForDeletion(task.id)
        }
    }

    fun Result<Unit>.registerUnitForDeletion(): Result<Unit> {
        return this.onSuccess { unit ->
            registerUnitForDeletion(unit.id)
        }
    }

    fun Result<PaymentRecord>.registerPaymentRecordForDeletion(): Result<PaymentRecord> {
        return this.onSuccess { record ->
            registerPaymentRecordForDeletion(record.id)
        }
    }

    fun Result<RentConfig>.registerRentConfigForDeletion(): Result<RentConfig> {
        return this.onSuccess { config ->
            registerRentConfigForDeletion(config.id)
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
                    results += membershipDatastore.cancelInvite(it)
                    results += membershipDatastore.purgeInvite(it)
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
                commonAreaResources.forEach {
                    results += commonAreaDatastore.deleteCommonArea(it)
                    results += commonAreaDatastore.purgeCommonArea(it)
                }
                taskResources.forEach {
                    results += taskDatastore.deleteTask(it)
                    results += taskDatastore.purgeTask(it)
                }
                paymentRecordResources.forEach {
                    results += paymentRecordDatastore.deletePaymentRecord(it)
                    results += paymentRecordDatastore.purgePaymentRecord(it)
                }
                rentConfigResources.forEach {
                    results += rentConfigDatastore.purgeRentConfig(it)
                }
                unitResources.forEach {
                    results += unitDatastore.deleteUnit(it)
                    results += unitDatastore.purgeUnit(it)
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
            unitResources.clear()
            paymentRecordResources.clear()
            rentConfigResources.clear()
            commonAreaResources.clear()
            taskResources.clear()
            propertyResources.clear()
            userResources.clear()
            organizationResources.clear()
            supabaseUsers.clear()
            stopKoin()
        }

        results.forEach { it.requireSuccess() }
    }
}
