package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.config.createJson
import com.codehavenx.platform.bot.controller.UserController
import com.codehavenx.platform.bot.service.UserService
import com.codehavenx.platform.bot.storage.UserDatabase
import com.mongodb.ConnectionString
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
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
        UserService(get<UserDatabase>())
    }

    single<() -> ObjectId> { { ObjectId() } }

    single<UserDatabase> {
        UserDatabase(get<MongoDatabase>(), get())
    }

    single<MongoClient> {
        val connectionString = ConnectionString("mongodb://localhost:27017")
        MongoClient.create(connectionString)
    }

    single<MongoDatabase> {
        get<MongoClient>().getDatabase("test")
    }
}
