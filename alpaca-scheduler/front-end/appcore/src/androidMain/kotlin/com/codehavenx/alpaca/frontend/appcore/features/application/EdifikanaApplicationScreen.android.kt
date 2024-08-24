package com.codehavenx.alpaca.frontend.appcore.features.application

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Composable that provides a Koin context for the given content within compose.
 */
@OptIn(KoinExperimentalAPI::class)
@Composable
internal actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinAndroidContext {
        content()
    }
}
