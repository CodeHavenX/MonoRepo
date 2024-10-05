package com.cramsan.edifikana.client.lib.features.main

import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext

/**
 * Composable Koin context.
 */
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinContext {
        content()
    }
}
