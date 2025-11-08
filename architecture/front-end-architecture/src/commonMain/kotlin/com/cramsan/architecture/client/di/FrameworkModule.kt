package com.cramsan.architecture.client.di

import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import org.koin.dsl.module

/**
 * Koin module for initializing core framework-level dependencies for front-end applications.
 * This module provides essential components like assertion utilities, event loggers,
 * halt utilities, thread utilities, and preferences.
 */
internal val FrameworkModule = module(createdAtStart = true) {

    single<AssertUtilInterface> {
        val settingsHolder: SettingsHolder = get()
        AssertUtilImpl(
            settingsHolder.getBoolean(FrontEndApplicationSettingKey.HaltOnFailure) ?: false,
            get(),
            get(),
        ).also {
            AssertUtil.setInstance(it)
        }
    }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(get(), get())
    }

    single<Severity> {
        val settingsHolder: SettingsHolder = get()

        Severity.fromStringOrDefault(
            settingsHolder.getString(FrontEndApplicationSettingKey.LoggingLevel),
            Severity.DEBUG,
        )
    }

    single<EventLoggerInterface> {
        EventLoggerImpl(
            get(),
            get(),
            get(),
        ).also {
            EventLogger.setInstance(it)
        }
    }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<ThreadUtilInterface> {
        ThreadUtilImpl(get())
    }

    single<Preferences> {
        PreferencesImpl(get())
    }
}
