package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.framework.test.asClock
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

/**
 * Class to initialize the integ test pplication level components.
 */
@OptIn(ExperimentalTime::class)
internal val IntegTestApplicationModule = module {

    single {
        TestTimeSource()
    }

    single<Clock> {
        val testTimeSource: TestTimeSource = get()
        testTimeSource.asClock(2024, 1, 1, 0, 0)
    }
}
