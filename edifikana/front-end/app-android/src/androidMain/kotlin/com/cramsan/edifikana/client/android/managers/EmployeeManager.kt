package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.client.android.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.android.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.android.models.EmployeeModel
import com.cramsan.edifikana.client.android.utils.getOrCatch
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeManager @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val workContext: WorkContext,
) {
    @OptIn(FireStoreModel::class)
    suspend fun getEmployees(): Result<List<EmployeeModel>> = workContext.getOrCatch {
        fireStore.collection(Employee.COLLECTION)
            .get()
            .await()
            .toObjects(Employee::class.java)
            .toList()
            .map { it.toDomainModel() }
    }

    @OptIn(FireStoreModel::class)
    suspend fun getEmployee(employeePK: EmployeePK): Result<EmployeeModel> = workContext.getOrCatch {
        fireStore.collection(Employee.COLLECTION)
            .document(employeePK.documentPath)
            .get()
            .await()
            .toObject(Employee::class.java)
            ?.toDomainModel() ?: throw RuntimeException("Employee $employeePK not found")
    }

    @OptIn(FireStoreModel::class)
    suspend fun addEmployee(employee: EmployeeModel) = workContext.getOrCatch {
        val firebaseModel = employee.toFirebaseModel()
        fireStore.collection(Employee.COLLECTION)
            .document(firebaseModel.documentId().documentPath)
            .set(firebaseModel)
            .await()
    }
}
