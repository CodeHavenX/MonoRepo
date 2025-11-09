package com.cramsan.architecture.server.test

import com.cramsan.architecture.server.test.dependencyinjection.TestFrameworkModule
import com.cramsan.architecture.server.test.dependencyinjection.TestKtorModule
import com.cramsan.architecture.server.test.dependencyinjection.testApplicationModule
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Wrap the ktor [testApplication] function and provide a test configuration.
 */
fun testBackEndApplication(
    configFile: String = "application-test.conf",
    block: suspend ApplicationTestBuilder.() -> Unit,
) = testApplication {
    environment {
        config = ApplicationConfig(configFile)
    }

    block()
}

/**
 * Start the koin framework with the test configuration.
 */
fun startTestKoin(
    json: Json,
    testControllerModule: Module,
    testServiceModule: Module,
    testKtorModule: Module = TestKtorModule,
    testFrameworkModule: Module = TestFrameworkModule,
    moduleDeclaration: Module.() -> Unit = {},
) {
    // Configure
    startKoin {
        modules(
            testFrameworkModule,
            testKtorModule,
            testServiceModule,
            testControllerModule,
            testApplicationModule(json),
            module(moduleDeclaration = moduleDeclaration),
        )
    }
}
