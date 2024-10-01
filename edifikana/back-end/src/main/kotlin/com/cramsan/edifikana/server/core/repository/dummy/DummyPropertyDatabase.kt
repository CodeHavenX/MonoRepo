@file:Suppress("MagicNumber")

package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.server.core.repository.PropertyDatabase
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.PropertyId
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
import kotlinx.coroutines.delay

/**
 * Dummy implementation of [PropertyDatabase].
 */
class DummyPropertyDatabase : PropertyDatabase {
    override suspend fun createProperty(request: CreatePropertyRequest): Result<Property> {
        delay(1000)
        return Result.success(
            Property(
                id = PropertyId("1"),
                name = "Test",
            )
        )
    }

    override suspend fun getProperty(request: GetPropertyRequest): Result<Property?> {
        delay(1000)
        return Result.success(
            Property(
                id = PropertyId("1"),
                name = "Test",
            )
        )
    }

    override suspend fun getProperties(): Result<List<Property>> {
        delay(1000)
        return Result.success(
            (0..10).map {
                Property(
                    id = PropertyId(it.toString()),
                    name = "Test $it",
                )
            }
        )
    }

    override suspend fun updateProperty(request: UpdatePropertyRequest): Result<Property> {
        delay(1000)
        return Result.success(
            Property(
                id = PropertyId("1"),
                name = "Test",
            )
        )
    }

    override suspend fun deleteProperty(request: DeletePropertyRequest): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }
}
