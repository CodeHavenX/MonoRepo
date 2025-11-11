package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.KoinAndroidContext

/**
 * Composable that provides the Koin context.
 */
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinAndroidContext {
        content()
    }
}
