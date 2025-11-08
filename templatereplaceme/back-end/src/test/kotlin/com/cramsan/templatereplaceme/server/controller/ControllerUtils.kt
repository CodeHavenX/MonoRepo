package com.cramsan.templatereplaceme.server.controller

import com.cramsan.templatereplaceme.server.dependencyinjection.testApplicationModule
import com.cramsan.templatereplaceme.server.dependencyinjection.testFrameworkModule
import com.cramsan.templatereplaceme.server.dependencyinjection.testKtorModule
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Wrap the ktor [testApplication] function and provide a test configuration.
 */
fun testTemplateReplaceMeApplication(
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
            testKtorModule(),
            testApplicationModule(),
            module(moduleDeclaration = moduleDeclaration),
        )
    }
}
