package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.db.AppDatabase
import org.koin.dsl.module

internal actual val DatabaseModule = module {
    // Room 3.x WASM driver not yet available — will throw if database is requested at runtime.
    single<AppDatabase> {
        error("Room database is not yet supported on WASM (Room 3.x WASM driver pending)")
    }
}
