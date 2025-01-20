package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.server.core.repository.PropertyDatabase
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
import com.cramsan.framework.logging.logD

/**
 * Class with dummy data to be used only for development and testing.
 */
class DummyPropertyDatabase : PropertyDatabase {
    override suspend fun createProperty(request: CreatePropertyRequest): Result<Property> {
        logD(TAG, "createProperty")
        return Result.success(PROPERTY_1)
    }

    override suspend fun getProperty(request: GetPropertyRequest): Result<Property?> {
        logD(TAG, "getProperty")
        return Result.success(PROPERTY_1)
    }

    override suspend fun getProperties(request: GetPropertyListsRequest): Result<List<Property>> {
        logD(TAG, "getProperties")
        return Result.success(listOf(PROPERTY_1, PROPERTY_2))
    }

    override suspend fun updateProperty(request: UpdatePropertyRequest): Result<Property> {
        logD(TAG, "updateProperty")
        return Result.success(PROPERTY_1)
    }

    override suspend fun deleteProperty(request: DeletePropertyRequest): Result<Boolean> {
        logD(TAG, "deleteProperty")
        return Result.success(true)
    }

    companion object {
        private const val TAG = "DummyPropertyDatabase"
    }
}
