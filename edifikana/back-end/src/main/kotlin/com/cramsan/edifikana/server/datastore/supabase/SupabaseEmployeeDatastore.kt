package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.EmployeeDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.EmployeeEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserPropertyMappingEntity
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Datastore for managing employee members.
 */
class SupabaseEmployeeDatastore(
    private val postgrest: Postgrest,
) : EmployeeDatastore {

    /**
     * Creates a new employee member for the given [request]. Returns the [Result] of the operation with the created [Employee].
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
     * Retrieves a employee member for the given [request]. Returns the [Result] of the operation with the fetched [Employee] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEmployee(
        id: EmployeeId,
    ): Result<Employee?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting employee: %s", id)

        val employeeEntity = postgrest.from(EmployeeEntity.COLLECTION).select {
            filter {
                EmployeeEntity::id eq id.empId
            }
        }.decodeSingleOrNull<EmployeeEntity>()

        employeeEntity?.toEmployee()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getEmployees(currentUser: UserId): Result<List<Employee>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all employee members")

        val propertyIds =
            postgrest.from(UserPropertyMappingEntity.COLLECTION).select {
                filter { UserPropertyMappingEntity::userId eq currentUser }
                select()
            }.decodeList<UserPropertyMappingEntity>().map { it.propertyId }

        postgrest.from(EmployeeEntity.COLLECTION).select {
            filter { EmployeeEntity::propertyId isIn propertyIds }
            select()
        }.decodeList<EmployeeEntity>().map { it.toEmployee() }
    }

    /**
     * Updates a employee member with the given [request]. Returns the [Result] of the operation with the updated [Employee].
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
                firstName?.let { value -> Employee::firstName setTo value }
                lastName?.let { value -> Employee::lastName setTo value }
                role?.let { value -> Employee::role setTo value }
                idType?.let { value -> Employee::idType setTo value }
            }
        ) {
            select()
            filter {
                EmployeeEntity::id eq employeeId
            }
        }.decodeSingle<EmployeeEntity>().toEmployee()
    }

    /**
     * Deletes a employee member with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteEmployee(
        id: EmployeeId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting employee: %s", id)

        postgrest.from(EmployeeEntity.COLLECTION).delete {
            select()
            filter {
                EmployeeEntity::id eq id
            }
        }.decodeSingleOrNull<EmployeeEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseEmployeeDatastore"
    }
}
