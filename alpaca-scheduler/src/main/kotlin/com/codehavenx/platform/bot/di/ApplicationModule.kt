package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.config.createJson
import com.codehavenx.platform.bot.controller.UserController
import com.codehavenx.platform.bot.service.UserService
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
}
