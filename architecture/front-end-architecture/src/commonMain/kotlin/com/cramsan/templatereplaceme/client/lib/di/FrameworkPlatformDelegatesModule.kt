package com.cramsan.templatereplaceme.client.lib.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin module for framework-level platform delegates.
 * Implementations vary by platform (Android, JVM, WasmJS) to provide platform-specific
 * implementations for logging, halting, threading, and other framework utilities.
 */
internal expect val FrameworkPlatformDelegatesModule: Module
