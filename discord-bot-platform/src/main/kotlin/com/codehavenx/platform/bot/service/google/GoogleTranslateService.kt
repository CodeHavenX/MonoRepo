package com.codehavenx.platform.bot.service.google

import com.cramsan.framework.logging.logI
import com.google.cloud.translate.Translate

class GoogleTranslateService(
    private val translate: Translate,
) {

    @Suppress("SpreadOperator")
    fun translate(message: String, sourceLanguage: Language, targetLanguage: Language): String {
        logI(TAG, "Calling translate. Length: ${message.length}")
        check(message.length > INPUT_CHAR_LIMIT) { "Message is too long" }

        val arrayOptions = listOfNotNull(
            sourceLanguage.let { Translate.TranslateOption.sourceLanguage(it.code) },
            Translate.TranslateOption.targetLanguage(targetLanguage.code),
        ).toTypedArray()

        return translate.translate(
            message,
            *arrayOptions,
        ).translatedText
    }

    companion object {
        private const val TAG = "GoogleTranslateService"
        private const val INPUT_CHAR_LIMIT = 120
    }
}
