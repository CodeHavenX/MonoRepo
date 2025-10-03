package com.cramsan.edifikana.client.lib.features.management.addrecord

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.model.EmployeeId

/**
 * Represents the UI model of the Add Record screen.
 */
data class AddRecordUIModel(
    val fullName: String,
    val employeePK: EmployeeId?,
)

/**
 * Converts a [EmployeeModel] to a [com.cramsan.edifikana.client.lib.features.management.addrecord.AddRecordUIModel].
 */
fun EmployeeModel.toUIModel(): AddRecordUIModel {
    return AddRecordUIModel(
        fullName = fullName(),
        employeePK = id,
    )
}
