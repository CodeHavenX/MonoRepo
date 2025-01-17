package com.cramsan.edifikana.client.lib.koin

import androidx.room.Room
import com.cramsan.edifikana.client.lib.db.AppDatabase
import com.cramsan.edifikana.client.lib.service.impl.AuthRequestPlugin
import com.cramsan.edifikana.lib.serialization.createJson
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val ExtrasPlatformModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "offline-db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    single { androidContext().contentResolver }

    single {
        HttpClient {
            defaultRequest {
                url("http://10.0.2.2:9292")
            }
            install(ContentNegotiation) {
                json(createJson())
            }
            install(AuthRequestPlugin(get()))
        }
    }
}
