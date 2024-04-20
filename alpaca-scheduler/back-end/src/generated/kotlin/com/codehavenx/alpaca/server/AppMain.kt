package com.codehavenx.alpaca.server

import com.codehavenx.alpaca.server.api.AppointmentApi
import com.codehavenx.alpaca.server.api.AppointmentConfigurationApi
import com.codehavenx.alpaca.server.api.AvailabilityApi
import com.codehavenx.alpaca.server.api.BusinessApi
import com.codehavenx.alpaca.server.api.UserApi
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.resources.Resources
import io.ktor.server.routing.Routing
import kotlinx.serialization.json.Json

fun Application.main(json: Json) {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        json(json)
    }
    install(AutoHeadResponse) // see https://ktor.io/docs/autoheadresponse.html
    install(Compression, ApplicationCompressionConfiguration()) // see https://ktor.io/docs/compression.html
    install(HSTS, ApplicationHstsConfiguration()) // see https://ktor.io/docs/hsts.html
    install(CORS) {
        allowHost("0.0.0.0:8282")
        allowHeader(HttpHeaders.ContentType)
        // The GET, POST and HEAD HTTP methods are allowed by default.
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }
    install(Resources)
    install(Routing) {
        AppointmentApi(json)
        AppointmentConfigurationApi(json)
        AvailabilityApi(json)
        BusinessApi(json)
        UserApi(json)
    }
}
