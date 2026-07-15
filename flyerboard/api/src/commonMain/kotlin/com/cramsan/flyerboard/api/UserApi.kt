package com.cramsan.flyerboard.api

import com.cramsan.flyerboard.lib.model.network.CreateUserNetworkRequest
import com.cramsan.flyerboard.lib.model.network.UserNetworkResponse
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for user-related endpoints.
 */

object UserApi : Api("user") {
    /** Operation to create a new user. */
    val createUser =
        operation<
            CreateUserNetworkRequest,
            NoQueryParam,
            NoPathParam,
            UserNetworkResponse,
            >(
            method = HttpMethod.Post,
            summary = "Create a user",
            description =
            "Creates the profile for the authenticated caller, with the USER role. " +
                "Called once after sign-up.",
            responses =
            AdditionalResponses {
                HttpStatusCode.Forbidden describedAs "Caller is not allowed to create this user profile."
            },
        )

    /** Operation to retrieve the currently authenticated user, including their role. */
    val getCurrentUser =
        operation<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            UserNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "me",
            summary = "Get the current user",
            description = "Retrieves the authenticated caller's profile, including their role.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No user profile exists for the authenticated caller."
            },
        )
}
