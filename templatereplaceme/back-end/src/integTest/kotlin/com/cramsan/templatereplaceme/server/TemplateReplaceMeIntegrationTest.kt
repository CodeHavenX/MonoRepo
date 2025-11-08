package com.cramsan.templatereplaceme.server

import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.server.dependencyinjection.FrameworkModule
import com.cramsan.templatereplaceme.server.dependencyinjection.IntegTestApplicationModule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
abstract class TemplateReplaceMeIntegrationTest : CoroutineTest(), KoinTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun classSetup() {
            startKoin {
                modules(
                    FrameworkModule,
                    IntegTestApplicationModule,
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
