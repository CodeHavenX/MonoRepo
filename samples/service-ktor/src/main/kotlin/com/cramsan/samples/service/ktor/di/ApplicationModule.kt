package com.cramsan.samples.service.ktor.di

import com.cramsan.samples.service.ktor.config.createJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
internal val ApplicationModule = module {

    single<CoroutineScope> {
        GlobalScope
    }

    single<Json> {
        createJson()
    }
}
