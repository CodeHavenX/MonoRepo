package com.cramsan.framework.sample.shared.features

import androidx.compose.runtime.Composable

/**
 * Application content with DI context.
 */
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    TODO("Implement DI context. Look at edifikana for reference.")
}
