package com.cramsan.edifikana.client.lib.di.koin

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.cramsan.edifikana.client.lib.db.AppDatabase
import com.cramsan.edifikana.client.lib.service.impl.AuthRequestPlugin
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

@Suppress("InjectDispatcher")
actual val ExtrasPlatformModule = module {

    single {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
        val builder = Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )

        builder
            .addMigrations()
            // TODO: Remove this once we have reached production
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single {
        HttpClient {
            // Handle custom exception response validation.
            expectSuccess = true
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, request ->
                    val clientException =
                        exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = clientException.response
                    when (exceptionResponse.status) {
                        HttpStatusCode.BadRequest -> {
                            val exceptionResponseText = exceptionResponse.bodyAsText()
                            throw ClientRequestExceptions.InvalidRequestException(exceptionResponseText)
                        }
                        HttpStatusCode.Unauthorized -> {
                            val exceptionResponseText = exceptionResponse.bodyAsText()
                            throw ClientRequestExceptions.UnauthorizedException(exceptionResponseText)
                        }
                        HttpStatusCode.Forbidden -> {
                            val exceptionResponseText = exceptionResponse.bodyAsText()
                            throw ClientRequestExceptions.ForbiddenException(exceptionResponseText)
                        }
                        HttpStatusCode.NotFound -> {
                            val exceptionResponseText = exceptionResponse.bodyAsText()
                            throw ClientRequestExceptions.NotFoundException(exceptionResponseText)
                        }
                        HttpStatusCode.Conflict -> {
                            val exceptionResponseText = exceptionResponse.bodyAsText()
                            throw ClientRequestExceptions.ConflictException(exceptionResponseText)
                        }
                        else -> {
                            // other codes handled until defined
                            val exceptionResponseText = exceptionResponse.bodyAsText()
                            throw exception
                        }
                    }
                }
            }
            defaultRequest {
                url("http://0.0.0.0:9292")
            }
            install(ContentNegotiation) {
                json(createJson())
            }

            val disableSupabase = get<Boolean>(named(Overrides.KEY_DISABLE_SUPABASE))
            if (!disableSupabase) {
                install(AuthRequestPlugin(get()))
            }
        }
    }
}
