package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.db.AppDatabase
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.logging.logE
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.datetime.Clock
import org.koin.dsl.module

val ExtrasModule = module {

    single<Clock> { Clock.System }

    single {
        CoroutineExceptionHandler { _, throwable ->
            logE("CoroutineExceptionHandler", "Uncaught Exception", throwable)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    single<CoroutineScope> { GlobalScope }

    single { get<AppDatabase>().eventLogRecordDao() }

    single { get<AppDatabase>().timeCardRecordDao() }

    single { get<AppDatabase>().fileAttachmentDao() }

    single {
        HttpClient {
            defaultRequest {
                url("http://0.0.0.0:9292")
            }
            install(ContentNegotiation) {
                json(createJson())
            }
        }
    }
}
