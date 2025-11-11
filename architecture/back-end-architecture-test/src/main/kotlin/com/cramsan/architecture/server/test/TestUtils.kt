package com.cramsan.architecture.server.test

import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.TestFrameworkModule
import com.cramsan.architecture.server.test.dependencyinjection.TestKtorModule
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
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
