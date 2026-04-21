package com.cramsan.flyerboard.client.lib.di

import org.koin.dsl.module

internal actual val DatabaseModule = module {
    // No database implementation for Wasm
}
