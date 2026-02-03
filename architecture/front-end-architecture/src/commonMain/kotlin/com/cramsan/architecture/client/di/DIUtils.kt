package com.cramsan.architecture.client.di

import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.EventEmitter
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

/**
 * Utility function to retrieve an EventEmitter instance from Koin DI.
 *
 * This function uses Koin's dependency injection to provide an EventEmitter
 * that is associated with the delegated event bus for a specific window.
 *
 * @return An EventEmitter of type T.
 */
@Composable
fun <T> koinEventEmitter(): EventEmitter<T> = koinInject(named(WindowIdentifier.DELEGATED_EVENT_BUS))