package com.cramsan.edifikana.client.lib.features.root

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Composable that provides the Koin context.
 */
@OptIn(KoinExperimentalAPI::class)
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinAndroidContext {
        content()
    }
}
