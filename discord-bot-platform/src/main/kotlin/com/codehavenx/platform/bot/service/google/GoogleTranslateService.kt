package com.codehavenx.platform.bot.service.google

import com.cramsan.framework.logging.logI
import com.google.cloud.translate.Translate

class GoogleTranslateService(
    private val translate: Translate,
) {

    @Suppress("SpreadOperator")
    fun translate(message: String, sourceLanguage: Language, targetLanguage: Language): String {
        logI(TAG, "Calling translate. Length: ${message.length}")
        if (message.length > 120) {
            throw IllegalStateException("Message is too long")
        }

        val arrayOptions = listOfNotNull(
            sourceLanguage?.let { Translate.TranslateOption.sourceLanguage(it.code) },
            Translate.TranslateOption.targetLanguage(targetLanguage.code),
        ).toTypedArray()

        return translate.translate(
            message,
            *arrayOptions,
        ).translatedText
    }

    companion object {
        private const val TAG = "GoogleTranslateService"
    }
}
