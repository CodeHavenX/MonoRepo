package com.cramsan.templatereplaceme.server

import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.integTestFrameworkModule
import com.cramsan.framework.test.CoroutineTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class UserDatastoreIntegrationTest : CoroutineTest(), KoinTest {

    @BeforeTest
    fun setUp() {
        // Any setup before each test can be done here.
        // Here you can load modules that you need for the tests
        startKoin {
            modules(
                TestArchitectureModule,
                integTestFrameworkModule("TEMPLATE_REPLACE_ME"),
            )
        }
    }

    @AfterTest
    fun tearDown() {
        // Clean up resources created during tests
        stopKoin()
    }
}