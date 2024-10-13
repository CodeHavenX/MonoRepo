package com.codehavenx.alpaca.frontend.appcore.managers

import com.codehavenx.alpaca.frontend.appcore.models.Staff
import com.codehavenx.alpaca.frontend.appcore.utils.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager to perform operations on staff.
 */
class StaffManager(
    private val workContext: WorkContext,
) {

    private val staffs = INITIAL_STAFF.toMutableList()

    /**
     * Get the list of staff.
     */
    suspend fun getStaff(): Result<List<Staff>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getStaff")
        staffs
    }

    /**
     * Add new staff.
     */
    suspend fun addStaff(staff: Staff): Result<Staff> = workContext.getOrCatch(TAG) {
        logI(TAG, "addStaff")
        staffs.add(staff)
        staff
    }

    /**
     * Update staff.
     */
    suspend fun updateStaff(client: Staff): Result<Staff> = workContext.getOrCatch(TAG) {
        logI(TAG, "updateStaff")
        val index = staffs.indexOfFirst { it.id == client.id }
        require(index == -1) { throw IllegalArgumentException("Staff not found") }
        staffs[index] = client
        client
    }

    /**
     * Delete staff.
     */
    suspend fun deleteStaff(client: Staff): Result<Staff> = workContext.getOrCatch(TAG) {
        logI(TAG, "deleteStaff")
        val index = staffs.indexOfFirst { it.id == client.id }
        require(index == -1) {
            throw IllegalArgumentException("Staff not found")
        }
        staffs.removeAt(index)
        client
    }

    /**
     * Get a staff by id.
     */
    suspend fun getStaffById(id: String): Result<Staff> = workContext.getOrCatch(TAG) {
        logI(TAG, "getStaffById")
        staffs.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Staff not found")
    }

    companion object {
        private const val TAG = "StaffManager"
    }
}

private val INITIAL_STAFF = listOf(
    Staff(
        id = "1",
        name = "John Doe",
        email = "john@gmail.com",
        phone = "345-345-3456",
        address = "123 Main St",
        city = "Portland",
        state = "OR",
        zip = "94272",
        country = "USA",
    ),
    Staff(
        id = "2",
        name = "Jane Doe",
        email = "jane@gmail.com",
        phone = "345-345-3456",
        address = "123 Main St",
        city = "Portland",
        state = "OR",
        zip = "94272",
        country = "USA",
    ),
    Staff(
        id = "3",
        name = "Alice Smith",
        email = "alice@gmail.com",
        phone = "123-456-7890",
        address = "456 Elm St",
        city = "Seattle",
        state = "WA",
        zip = "98101",
        country = "USA",
    ),
    Staff(
        id = "4",
        name = "Bob Johnson",
        email = "bob@gmail.com",
        phone = "234-567-8901",
        address = "789 Oak St",
        city = "San Francisco",
        state = "CA",
        zip = "94102",
        country = "USA",
    ),
    Staff(
        id = "5",
        name = "Carol White",
        email = "carol@gmail.com",
        phone = "345-678-9012",
        address = "101 Pine St",
        city = "Los Angeles",
        state = "CA",
        zip = "90001",
        country = "USA",
    ),
    Staff(
        id = "6",
        name = "David Brown",
        email = "david@gmail.com",
        phone = "456-789-0123",
        address = "202 Maple St",
        city = "Chicago",
        state = "IL",
        zip = "60601",
        country = "USA",
    ),
    Staff(
        id = "7",
        name = "Eve Davis",
        email = "eve@gmail.com",
        phone = "567-890-1234",
        address = "303 Birch St",
        city = "Houston",
        state = "TX",
        zip = "77001",
        country = "USA",
    ),
    Staff(
        id = "8",
        name = "Frank Green",
        email = "frank@gmail.com",
        phone = "678-901-2345",
        address = "404 Cedar St",
        city = "Phoenix",
        state = "AZ",
        zip = "85001",
        country = "USA",
    ),
    Staff(
        id = "9",
        name = "Grace Hall",
        email = "grace@gmail.com",
        phone = "789-012-3456",
        address = "505 Spruce St",
        city = "Denver",
        state = "CO",
        zip = "80201",
        country = "USA",
    ),
    Staff(
        id = "10",
        name = "Hank King",
        email = "hank@gmail.com",
        phone = "890-123-4567",
        address = "606 Willow St",
        city = "Miami",
        state = "FL",
        zip = "33101",
        country = "USA",
    ),
    Staff(
        id = "11",
        name = "Ivy Lee",
        email = "ivy@gmail.com",
        phone = "901-234-5678",
        address = "707 Ash St",
        city = "Atlanta",
        state = "GA",
        zip = "30301",
        country = "USA",
    ),
    Staff(
        id = "12",
        name = "Jack Moore",
        email = "jack@gmail.com",
        phone = "012-345-6789",
        address = "808 Poplar St",
        city = "Boston",
        state = "MA",
        zip = "02101",
        country = "USA",
    )
)
