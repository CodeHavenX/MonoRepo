package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.client.android.BackgroundDispatcher
import com.cramsan.edifikana.client.android.run
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.IdType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await

@Singleton
class EmployeeManager @Inject constructor(
    val fireStore: FirebaseFirestore,
    @BackgroundDispatcher
    val background: CoroutineDispatcher,
) {
    suspend fun getEmployees(): Result<List<Employee>> = background.run {
        fireStore.collection(Employee.COLLECTION)
            .get()
            .await()
            .toObjects(Employee::class.java).toList()
    }

    suspend fun getEmployee(employeePK: EmployeePK): Result<Employee> = background.run {
        fireStore.collection(Employee.COLLECTION)
            .document(employeePK.documentPath)
            .get()
            .await()
            .toObject(Employee::class.java) ?: throw Exception("Employee not found")
    }

    suspend fun addEmployee(employee: Employee) = background.run {
        fireStore.collection(Employee.COLLECTION)
            .document(employee.documentId().documentPath)
            .set(employee)
            .await()
    }
}