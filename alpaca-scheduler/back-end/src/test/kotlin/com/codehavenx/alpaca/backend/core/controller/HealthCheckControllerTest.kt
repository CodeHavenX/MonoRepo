package com.codehavenx.alpaca.backend.core.controller

import com.cramsan.framework.test.TestBase
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthCheckControllerTest : TestBase(), KoinTest {

    override fun setupTest() = Unit

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test healthCheck`() = testAlpacaApplication {
        // Configure
        startTestKoin {
            single { HealthCheckController() }
        }

        // Act
        val response = client.get("health")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }
}
