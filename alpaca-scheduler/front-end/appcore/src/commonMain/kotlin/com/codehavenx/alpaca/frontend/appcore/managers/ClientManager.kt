package com.codehavenx.alpaca.frontend.appcore.managers

import com.codehavenx.alpaca.frontend.appcore.models.Client
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager to perform operations on clients.
 */
class ClientManager(
    private val dependencies: ManagerDependencies,
) {

    private val clients = INITIAL_CLIENTS.toMutableList()

    /**
     * Get the list of clients.
     */
    suspend fun getClients(): Result<List<Client>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getClients")
        clients
    }

    /**
     * Add new client.
     */
    suspend fun addClient(client: Client): Result<Client> = dependencies.getOrCatch(TAG) {
        logI(TAG, "addClient")
        clients.add(client)
        client
    }

    /**
     * Update client.
     */
    suspend fun updateClient(client: Client): Result<Client> = dependencies.getOrCatch(TAG) {
        logI(TAG, "updateClient")
        val index = clients.indexOfFirst { it.id == client.id }
        require(index == -1) {
            throw IllegalArgumentException("Client not found")
        }
        clients[index] = client
        client
    }

    /**
     * Delete client.
     */
    suspend fun deleteClient(client: Client): Result<Client> = dependencies.getOrCatch(TAG) {
        logI(TAG, "deleteClient")
        val index = clients.indexOfFirst { it.id == client.id }
        require(index == -1) {
            throw IllegalArgumentException("Client not found")
        }
        clients.removeAt(index)
        client
    }

    /**
     * Get a client by id.
     */
    suspend fun getClientById(id: String): Result<Client> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getClientById")
        clients.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Client not found")
    }

    companion object {
        private const val TAG = "ClientManager"
    }
}

private val INITIAL_CLIENTS = listOf(
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
    Client(
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
