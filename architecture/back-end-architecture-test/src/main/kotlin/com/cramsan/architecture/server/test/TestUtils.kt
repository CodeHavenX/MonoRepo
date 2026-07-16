package com.cramsan.architecture.server.test

import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.TestFrameworkModule
import com.cramsan.architecture.server.test.dependencyinjection.TestKtorModule
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Bearer token sent by the authenticated test client. Any non-blank value works: the test
 * [com.cramsan.framework.core.ktor.auth.ContextRetriever] is mocked, so the token itself is not
 * validated — its presence just lets the bearer authentication provider run and resolve the mocked
 * context.
 */
const val TEST_BEARER_TOKEN = "test-token"

/**
 * Wrap the ktor [testApplication] function and provide a test configuration.
 *
 * The [block] receives an [HttpClient] pre-configured to send an `Authorization: Bearer` header, so
 * requests to authenticated routes pass the bearer authentication gate. Name the parameter `client`
 * to use it in place of the default (unauthenticated) test client.
 */
fun testBackEndApplication(
    configFile: String = "application-test.conf",
    block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit,
) = testApplication {
    environment {
        config = ApplicationConfig(configFile)
    }

    val authenticatedClient =
        createClient {
            install(DefaultRequest) {
                header(HttpHeaders.Authorization, "Bearer $TEST_BEARER_TOKEN")
            }
        }

    block(authenticatedClient)
}

/**
 * Start the koin framework with the test configuration.
 */
fun startTestKoin(
    testApplicationModule: Module,
    testControllerModule: Module,
    testServiceModule: Module,
    testArchitectureModule: Module = TestArchitectureModule,
    testKtorModule: Module = TestKtorModule,
    testFrameworkModule: Module = TestFrameworkModule,
    moduleDeclaration: Module.() -> Unit = {},
) {
    // Configure
    startKoin {
        modules(
            testFrameworkModule,
            testKtorModule,
            testArchitectureModule,
            testServiceModule,
            testControllerModule,
            testApplicationModule,
            module(moduleDeclaration = moduleDeclaration),
        )
    }
}
