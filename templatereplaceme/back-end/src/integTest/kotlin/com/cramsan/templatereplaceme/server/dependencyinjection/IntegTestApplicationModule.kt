package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.templatereplaceme.server.SettingsHolder
import com.cramsan.framework.test.asClock
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

/**
 * Class to initialize the integ test application level components.
 */
@OptIn(ExperimentalTime::class)
val IntegTestApplicationModule = module {

    single<String>(named(NamedDependency.STAGE_KEY)) {
        "integ"
    }

    single {
        TestTimeSource()
    }

    single<Clock> {
        val testTimeSource: TestTimeSource = get()
        testTimeSource.asClock(2024, 1, 1, 0, 0)
    }

    single {
        SettingsHolder(get())
    }
}

