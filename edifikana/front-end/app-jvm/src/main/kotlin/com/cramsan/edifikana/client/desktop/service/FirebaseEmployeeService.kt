package com.cramsan.edifikana.client.desktop.service

import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirebaseEmployeeService(
    private val fireStore: FirebaseFirestore,
) : EmployeeService {

    @OptIn(FireStoreModel::class)
    override suspend fun getEmployees(): Result<List<EmployeeModel>> = runSuspendCatching {
        fireStore.collection(Employee.COLLECTION)
            .get()
            .documents
            .map { it.data<Employee>() }
            .map { it.toDomainModel() }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getEmployee(employeePK: EmployeePK): Result<EmployeeModel> = runSuspendCatching {
        fireStore.collection(Employee.COLLECTION)
            .document(employeePK.documentPath)
            .get()
            .data<Employee>()
            .toDomainModel()
    }

    @OptIn(FireStoreModel::class)
    override suspend fun addEmployee(employee: EmployeeModel): Result<Unit> = runSuspendCatching {
        val firebaseModel = employee.toFirebaseModel()
        fireStore.collection(Employee.COLLECTION)
            .document(firebaseModel.documentId().documentPath)
            .set(firebaseModel)
    }
}
