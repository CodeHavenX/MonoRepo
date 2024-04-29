package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.IdType
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await


class FireStoreManager() {

    val db = Firebase.firestore

    val results = db.collection(Employee.COLLECTION).snapshots()



    suspend fun getEmployees() {
        val employee = Employee("1", IdType.DNI, "John", "Doe")
        db.collection(Employee.COLLECTION).document(employee.documentId()).set(employee).await()
        results.collect { snapshot ->
            snapshot.documents.map { document ->
                val employee = document.toObject<Employee>()
                println(employee)
            }
        }

        val city = hashMapOf(
            "name" to "Los Angeles",
            "state" to "CA",
            "country" to "USA",
        )

        db.collection("cities").document("LA")
            .set(city)
            .await()

        val docRef = db.collection("cities").document("LA")
        val cities = docRef.get().await().toObject<City>()

        println(cities)

        val docRef2 = db.collection(Employee.COLLECTION).document(employee.documentId())
        val cities2 = docRef2.get().await().toObject<Employee>()
        println(cities2)
    }

}

data class City(
    val name: String? = null,
    val state: String? = null,
    val country: String? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isCapital: Boolean? = null,
    val population: Long? = null,
    val regions: List<String>? = null,
)