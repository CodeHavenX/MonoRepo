package com.codehavenx.alpaca.frontend.appcore.features.application

import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext

@Composable
internal actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinContext {
        content()
    }
}
