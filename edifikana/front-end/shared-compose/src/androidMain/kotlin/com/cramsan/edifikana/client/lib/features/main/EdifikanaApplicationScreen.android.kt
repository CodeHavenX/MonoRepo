package com.cramsan.edifikana.client.lib.features.main

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinAndroidContext {
        content()
    }
}
