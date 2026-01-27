package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CreateEmployeeNetworkRequest
import com.cramsan.edifikana.lib.model.network.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateEmployeeNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.framework.annotations.NetworkModel

/**
 * Maps the [EventLogEntryNetworkResponse] models to [EventLogRecordModel] domain models.
 */
@OptIn(NetworkModel::class)
fun EventLogEntryNetworkResponse.toEventLogRecordModel(): EventLogRecordModel = EventLogRecordModel(
    id = id,
    entityId = null,
    employeePk = employeeId,
    propertyId = propertyId,
    timeRecorded = timestamp,
    unit = unit,
    eventType = type,
    fallbackEmployeeName = fallbackEventType,
    fallbackEventType = fallbackEmployeeName,
    title = title,
    description = description.orEmpty(),
    emptyList(),
)

/**
 * Maps the [EventLogRecordModel] domain models to [CreateEventLogEntryNetworkRequest] models.
 */
@OptIn(NetworkModel::class)
fun EventLogRecordModel.toCreateEventLogEntryNetworkRequest(): CreateEventLogEntryNetworkRequest =
    CreateEventLogEntryNetworkRequest(
        employeeId = employeePk,
        fallbackEmployeeName = fallbackEmployeeName,
        propertyId = propertyId,
        type = eventType,
        fallbackEventType = fallbackEventType,
        timestamp = timeRecorded,
        title = title,
        description = description,
        unit = unit,
    )

/**
 * Maps the [EventLogRecordModel] domain models to [UpdateEventLogEntryNetworkRequest] models.
 */
@OptIn(NetworkModel::class)
fun EventLogRecordModel.toUpdateEventLogEntryNetworkRequest(): UpdateEventLogEntryNetworkRequest =
    UpdateEventLogEntryNetworkRequest(
        type = eventType,
        fallbackEventType = fallbackEventType,
        title = title,
        description = description,
        unit = unit,
    )

/**
 * Maps the [EmployeeModel.CreateEmployeeRequest] models to [CreateEmployeeNetworkRequest] models.
 */
@NetworkModel
fun EmployeeModel.CreateEmployeeRequest.toCreateEmployeeNetworkRequest(): CreateEmployeeNetworkRequest =
    CreateEmployeeNetworkRequest(
        idType = idType,
        firstName = firstName,
        lastName = lastName,
        role = role,
        propertyId = propertyId,
    )

/**
 * Maps the [EmployeeModel.UpdateEmployeeRequest] models to [UpdateEmployeeNetworkRequest] models.
 * Note: IdType is not updatable, so it is set to null.
 */
@NetworkModel
fun EmployeeModel.UpdateEmployeeRequest.toUpdateEmployeeNetworkRequest(): UpdateEmployeeNetworkRequest =
    UpdateEmployeeNetworkRequest(
        idType = null, // IdType is not updatable
        firstName = firstName,
        lastName = lastName,
        role = role,
    )

/**
 * Maps the [EmployeeNetworkResponse] models to [EmployeeModel] domain models.
 */
@NetworkModel
fun EmployeeNetworkResponse.toEmployeeModel(): EmployeeModel = EmployeeModel(
    id = id,
    firstName = firstName,
    lastName = lastName,
    role = role,
    idType = idType,
    email = null,
)

/**
 * Maps the [TimeCardRecordModel] domain models to [CreateTimeCardEventNetworkRequest] models.
 */
@NetworkModel
fun TimeCardRecordModel.toCreateTimeCardEventNetworkRequest(): CreateTimeCardEventNetworkRequest =
    CreateTimeCardEventNetworkRequest(
        employeeId = employeePk,
        fallbackEmployeeName = "",
        type = eventType,
        propertyId = propertyId,
        imageUrl = imageUrl,
    )

/**
 * Maps the [TimeCardEventNetworkResponse] models to [TimeCardRecordModel] domain models.
 */
@NetworkModel
fun TimeCardEventNetworkResponse.toTimeCardRecordModel(): TimeCardRecordModel = TimeCardRecordModel(
    id = id,
    entityId = null,
    employeePk = employeeId!!, // TODO: Fix this
    propertyId = propertyId,
    eventType = type,
    eventTime = timestamp,
    imageUrl = imageUrl,
    imageRef = null,
)

/**
 * Maps the [UserNetworkResponse] models to [UserModel] domain models.
 */
@NetworkModel
fun UserNetworkResponse.toUserModel(): UserModel = UserModel(
    id = UserId(id),
    email = email,
    phoneNumber = phoneNumber,
    firstName = firstName,
    lastName = lastName,
    authMetadata = authMetadata?.let {
        UserModel.AuthMetadataModel(
            isPasswordSet = it.isPasswordSet,
        )
    },
)

/**
 * Maps the [OrganizationNetworkResponse] models to [Organization] domain models.
 */
@OptIn(NetworkModel::class)
fun OrganizationNetworkResponse.toOrganizationModel(): Organization = Organization(
    id = id,
    name = name,
    description = description,
)
