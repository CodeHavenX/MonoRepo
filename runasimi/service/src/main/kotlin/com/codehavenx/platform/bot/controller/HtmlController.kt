package com.codehavenx.platform.bot.controller

import com.codehavenx.platform.bot.service.TranslationService
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.body
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.submitInput
import kotlinx.html.textInput
import kotlinx.html.title

/**
 * This controller will load [modules] as a list of available webhoks and their respective handlers.
 */
class HtmlController {

    /**
     * This function registers the routes for all the [modules]. The [route] is the root path.
     */
    fun registerRoutes(route: Route) {
        route.apply {
            get {
                showHtml(call)
            }
        }
    }

    /**
     * Handles the request provided by [call] for the webhook of [entryPoint]. This function takes care of deserializing
     * the body and provides it the respective [WebhookEntryPoint].
     */
    private suspend fun showHtml(call: ApplicationCall) {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"name"
                }
            }
            body {
                h1 {
                    +"Hello from name!"
                }
                form(action = "/tts-form", encType = FormEncType.multipartFormData, method = FormMethod.post) {
                    p {
                        +"Message:"
                        textInput(name = ApiController.FORM_KEY_MESSAGE)
                    }
                    p {
                        submitInput { value = "Submit" }
                    }
                }
            }
        }
    }
}
