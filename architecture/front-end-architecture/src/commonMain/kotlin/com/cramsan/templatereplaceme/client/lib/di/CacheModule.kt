package com.cramsan.templatereplaceme.client.lib.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin module for cache-related dependencies.
 * Implementations vary by platform (Android, JVM, WasmJS) to provide appropriate caching mechanisms.
 */
internal expect val CacheModule: Module
