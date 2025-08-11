package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.server.di.testApplicationModule
import com.cramsan.edifikana.server.di.testFrameworkModule
import com.cramsan.edifikana.server.di.testKtorModule
import com.cramsan.edifikana.server.di.testSettingsModule
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Wrap the ktor [testApplication] function and provide a test configuration.
 */
fun testEdifikanaApplication(
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
    moduleDeclaration: Module.() -> Unit = {},
) {
    // Configure
    startKoin {
        modules(
            testFrameworkModule(),
            testSettingsModule(),
            testKtorModule(),
            testApplicationModule(),
            module(moduleDeclaration = moduleDeclaration),
        )
    }
}
