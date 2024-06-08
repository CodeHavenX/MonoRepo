package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseEmployeeService(
    private val fireStore: FirebaseFirestore,
) : EmployeeService {

    @OptIn(FireStoreModel::class)
    override suspend fun getEmployees(): Result<List<EmployeeModel>> = runSuspendCatching {
        fireStore.collection(Employee.COLLECTION)
            .get()
            .await()
            .toObjects(Employee::class.java)
            .toList()
            .map { it.toDomainModel() }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getEmployee(employeePK: EmployeePK): Result<EmployeeModel> = runSuspendCatching {
        fireStore.collection(Employee.COLLECTION)
            .document(employeePK.documentPath)
            .get()
            .await()
            .toObject(Employee::class.java)
            ?.toDomainModel() ?: throw RuntimeException("Employee $employeePK not found")
    }

    @OptIn(FireStoreModel::class)
    override suspend fun addEmployee(employee: EmployeeModel): Result<Unit> = runSuspendCatching {
        val firebaseModel = employee.toFirebaseModel()
        fireStore.collection(Employee.COLLECTION)
            .document(firebaseModel.documentId().documentPath)
            .set(firebaseModel)
            .await()
    }
}
