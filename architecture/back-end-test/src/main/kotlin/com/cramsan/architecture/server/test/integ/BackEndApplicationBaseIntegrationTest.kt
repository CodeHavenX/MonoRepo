package com.cramsan.architecture.server.test.integ

import com.cramsan.architecture.server.test.dependencyinjection.IntegTestFrameworkModule
import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.framework.test.CoroutineTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.time.ExperimentalTime

@Suppress("UnnecessaryAbstractClass")
@OptIn(ExperimentalTime::class)
abstract class BackEndApplicationBaseIntegrationTest : CoroutineTest(), KoinTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun classSetup() {
            startKoin {
                modules(
                    IntegTestFrameworkModule,
                    TestArchitectureModule,
                )
            }
        }

        @AfterAll
        @JvmStatic
        fun classTearDown() {
            stopKoin()
        }
    }
}
