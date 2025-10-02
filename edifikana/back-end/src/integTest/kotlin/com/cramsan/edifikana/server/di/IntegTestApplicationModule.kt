package com.cramsan.edifikana.server.di

import com.cramsan.framework.test.asClock
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

/**
 * Class to initialize the integ test pplication level components.
 */
@OptIn(ExperimentalTime::class)
val IntegTestApplicationModule = module {
    single<String>(named(STAGE_KEY)) {
        "integ"
    }

    single {
        TestTimeSource()
    }

    single<Clock> {
        var testTimeSource: TestTimeSource = get()
        testTimeSource.asClock(2024, 1, 1, 0, 0)
    }
}

