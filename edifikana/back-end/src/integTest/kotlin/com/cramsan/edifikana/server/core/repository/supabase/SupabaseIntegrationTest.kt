package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.di.FrameworkModule
import com.cramsan.edifikana.server.di.IntegTestApplicationModule
import com.cramsan.edifikana.server.di.SettingsModule
import com.cramsan.edifikana.server.di.SupabaseModule
import com.cramsan.framework.test.CoroutineTest
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

abstract class SupabaseIntegrationTest : CoroutineTest(), KoinTest {

    protected val supabase: SupabaseClient by inject()

    protected val eventLogDatabase: SupabaseEventLogDatabase by inject()
    protected val propertyDatabase: SupabasePropertyDatabase by inject()
    protected val staffDatabase: SupabaseStaffDatabase by inject()
    protected val timeCardDatabase: SupabaseTimeCardDatabase by inject()
    protected val userDatabase: SupabaseUserDatabase by inject()

    private val eventLogResources = mutableSetOf<EventLogEntryId>()
    private val propertyResources = mutableSetOf<PropertyId>()
    private val staffResources = mutableSetOf<StaffId>()
    private val timeCardResources = mutableSetOf<TimeCardEventId>()
    private val userResources = mutableSetOf<UserId>()
    private val supabaseUsers = mutableSetOf<String>()

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

    private fun registerStaffForDeletion(staffId: StaffId) {
        staffResources.add(staffId)
    }

    private fun registerTimeCardEventForDeletion(timeCardId: TimeCardEventId) {
        timeCardResources.add(timeCardId)
    }

    private fun registerUserForDeletion(userId: UserId) {
        userResources.add(userId)
    }

    protected fun createTestProperty(name: String): PropertyId {
        val propertyId = runBlocking {
            propertyDatabase.createProperty(CreatePropertyRequest(name)).getOrThrow().id
        }
        registerPropertyForDeletion(propertyId)
        return propertyId
    }

    fun Result<Property>.registerPropertyForDeletion(): Result<Property> {
        return this.onSuccess { property ->
            registerPropertyForDeletion(property.id)
        }
    }

    fun Result<Staff>.registerStaffForDeletion(): Result<Staff> {
        return this.onSuccess { staff ->
            registerStaffForDeletion(staff.id)
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

    fun registerSupabaseUserForDeletion(userId: String) {
        supabaseUsers.add(userId)
    }

    protected fun createTestStaff(
        propertyId: PropertyId,
        firstName: String,
        lastName: String,
    ): StaffId {
        val staffId = runBlocking {
            staffDatabase.createStaff(
                CreateStaffRequest(
                    propertyId = propertyId,
                    firstName = firstName,
                    lastName = lastName,
                    role = StaffRole.SECURITY, // Use a valid role
                    idType = IdType.PASSPORT // Use a valid ID type
                )
            ).getOrThrow().id
        }
        registerStaffForDeletion(staffId)
        return staffId
    }

    @AfterTest
    fun tearDown() {
        // Clean up resources created during tests
        // The order matters since there are foreign key constraints
        runBlocking {
            supabase.auth.signOut()
            supabase.auth.clearSession()

            eventLogResources.forEach {
                eventLogDatabase.deleteEventLogEntry(DeleteEventLogEntryRequest(it)).getOrThrow()
            }
            timeCardResources.forEach {
                timeCardDatabase.deleteTimeCardEvent(DeleteTimeCardEventRequest(it)).getOrThrow()
            }
            staffResources.forEach {
                staffDatabase.deleteStaff(DeleteStaffRequest(it)).getOrThrow()
            }
            propertyResources.forEach {
                propertyDatabase.deleteProperty(DeletePropertyRequest(it)).getOrThrow()
            }
            userResources.forEach {
                userDatabase.deleteUser(DeleteUserRequest(it)).getOrThrow()
            }
            supabaseUsers.forEach { userId ->
                supabase.auth.admin.deleteUser(userId)
            }
        }
        eventLogResources.clear()
        timeCardResources.clear()
        staffResources.clear()
        propertyResources.clear()
        userResources.clear()
        supabaseUsers.clear()
    }
}
