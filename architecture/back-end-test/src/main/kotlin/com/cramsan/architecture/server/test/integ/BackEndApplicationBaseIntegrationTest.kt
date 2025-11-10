package com.cramsan.architecture.server.test.integ

import com.cramsan.architecture.server.test.dependencyinjection.IntegTestFrameworkModule
import com.cramsan.architecture.server.test.dependencyinjection.testApplicationModule
import com.cramsan.framework.test.CoroutineTest
import kotlinx.serialization.json.Json
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
                    testApplicationModule(Json, stageKey = "integ"),
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
