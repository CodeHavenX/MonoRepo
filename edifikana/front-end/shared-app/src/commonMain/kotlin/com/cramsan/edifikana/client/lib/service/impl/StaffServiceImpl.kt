package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.client.lib.service.impl.PropertyServiceImpl.Companion.TAG
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.network.StaffNetworkResponse
import com.cramsan.framework.core.runSuspendCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Default implementation for the [StaffService].
 */
class StaffServiceImpl(
    private val http: HttpClient,
) : StaffService {

    @OptIn(NetworkModel::class)
    override suspend fun getStaffList(): Result<List<StaffModel>> = runSuspendCatching(TAG) {
        val response = http.get(Routes.Staff.PATH).body<List<StaffNetworkResponse>>()
        val staffList = response.map {
            it.toStaffModel()
        }
        staffList
    }

    @OptIn(NetworkModel::class)
    override suspend fun getStaff(staffPK: StaffId): Result<StaffModel> = runSuspendCatching(TAG) {
        val response = http.get("${Routes.Staff.PATH}/${staffPK.staffId}").body<StaffNetworkResponse>()
        val staff = response.toStaffModel()
        staff
    }

    @OptIn(NetworkModel::class)
    override suspend fun createStaff(
        staff: StaffModel.CreateStaffRequest,
    ): Result<StaffModel> = runSuspendCatching(TAG) {
        val response = http.post(Routes.Staff.PATH) {
            contentType(ContentType.Application.Json)
            setBody(staff.toCreateStaffNetworkRequest())
        }.body<StaffNetworkResponse>()
        val staffModel = response.toStaffModel()
        staffModel
    }

    override suspend fun inviteStaff(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}
