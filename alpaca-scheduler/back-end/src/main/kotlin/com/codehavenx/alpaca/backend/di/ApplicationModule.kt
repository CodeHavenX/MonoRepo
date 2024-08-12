package com.codehavenx.alpaca.backend.di

import com.codehavenx.alpaca.backend.config.createJson
import com.codehavenx.alpaca.backend.controller.UserController
import com.codehavenx.alpaca.backend.service.UserService
import com.codehavenx.alpaca.backend.storage.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
val ApplicationModule = module(createdAtStart = true) {

    single<CoroutineScope> {
        GlobalScope
    }

    single<Json> {
        createJson()
    }

    single<UserController> {
        UserController(get())
    }

    single<UserService> {
        UserService(get())
    }

    single<UserDatabase> {
        UserDatabase()
    }
}
