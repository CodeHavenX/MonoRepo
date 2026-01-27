package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.EmployeeDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.EmployeeEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserEmployeeViewEntity
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock

/**
 * Datastore for managing employee members.
 */
class SupabaseEmployeeDatastore(private val postgrest: Postgrest, private val clock: Clock) : EmployeeDatastore {

    /**
     * Creates a new employee member. Returns the created [Employee].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createEmployee(
        idType: IdType,
        firstName: String,
        lastName: String,
        role: EmployeeRole,
        propertyId: PropertyId,
    ): Result<Employee> = runSuspendCatching(TAG) {
        logD(TAG, "Creating employee: %s", firstName)
        val requestEntity: EmployeeEntity.CreateEmployeeEntity = CreateEmployeeEntity(
            idType = idType,
            firstName = firstName,
            lastName = lastName,
            role = role,
            propertyId = propertyId,
        )

        val createdEmployee = postgrest.from(EmployeeEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<EmployeeEntity>()
        logD(TAG, "Employee created employeeId: %s", createdEmployee.id)
        createdEmployee.toEmployee()
    }

    /**
     * Retrieves an employee by [id]. Returns the [Employee] if found, null otherwise.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEmployee(id: EmployeeId): Result<Employee?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting employee: %s", id)

        val employeeEntity = postgrest.from(EmployeeEntity.COLLECTION).select {
            filter {
                EmployeeEntity::id eq id.empId
                EmployeeEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<EmployeeEntity>()

        employeeEntity?.toEmployee()
    }

    /**
     * Gets all employees accessible to the given user.
     * Uses the v_user_employees view for single-query retrieval (eliminates N+1 pattern).
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEmployees(currentUser: UserId): Result<List<Employee>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all employees for user: %s", currentUser)

        // Use the v_user_employees view for single-query retrieval
        postgrest.from(VIEW_USER_EMPLOYEES).select {
            filter { UserEmployeeViewEntity::userId eq currentUser.userId }
        }.decodeList<UserEmployeeViewEntity>().map { it.toEmployee() }
    }

    /**
     * Updates an employee's properties. Only non-null parameters are updated.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateEmployee(
        employeeId: EmployeeId,
        idType: IdType?,
        firstName: String?,
        lastName: String?,
        role: EmployeeRole?,
    ): Result<Employee> = runSuspendCatching(TAG) {
        logD(TAG, "Updating employee: %s", employeeId)

        postgrest.from(EmployeeEntity.COLLECTION).update(
            {
                firstName?.let { value -> EmployeeEntity::firstName setTo value }
                lastName?.let { value -> EmployeeEntity::lastName setTo value }
                role?.let { value -> EmployeeEntity::role setTo value }
                idType?.let { value -> EmployeeEntity::idType setTo value }
            },
        ) {
            select()
            filter {
                EmployeeEntity::id eq employeeId.empId
            }
        }.decodeSingle<EmployeeEntity>().toEmployee()
    }

    /**
     * Soft deletes an employee by [id]. Returns true if successful.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteEmployee(id: EmployeeId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting employee: %s", id)

        postgrest.from(EmployeeEntity.COLLECTION).update({
            EmployeeEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                EmployeeEntity::id eq id.empId
                EmployeeEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<EmployeeEntity>() != null
    }

    /**
     * Permanently deletes a soft-deleted employee by [id]. Returns true if successful.
     * Only purges records that are already soft-deleted (deletedAt is not null).
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeEmployee(id: EmployeeId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging soft-deleted employee: %s", id)

        // First verify the record exists and is soft-deleted
        val entity = postgrest.from(EmployeeEntity.COLLECTION).select {
            filter {
                EmployeeEntity::id eq id.empId
            }
        }.decodeSingleOrNull<EmployeeEntity>()

        // Only purge if it exists and is soft-deleted
        if (entity?.deletedAt == null) {
            return@runSuspendCatching false
        }

        // Delete the record
        postgrest.from(EmployeeEntity.COLLECTION).delete {
            filter {
                EmployeeEntity::id eq id.empId
            }
        }
        true
    }

    companion object {
        const val TAG = "SupabaseEmployeeDatastore"
        const val VIEW_USER_EMPLOYEES = "v_user_employees"
    }
}
