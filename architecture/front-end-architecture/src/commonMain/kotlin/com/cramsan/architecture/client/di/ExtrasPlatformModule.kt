package com.cramsan.architecture.client.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin module for additional platform-dependent components.
 * Implementations vary by platform (Android, JVM, WasmJS) to provide platform-specific functionality.
 */
internal expect val ExtrasPlatformModule: Module
