package com.cramsan.edifikana.client.lib.features.root

import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext

/**
 * Edifikana main screen event handler.
 */
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinContext {
        content()
    }
}