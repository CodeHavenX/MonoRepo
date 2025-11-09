package com.cramsan.templatereplaceme.client.lib.di

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
import com.cramsan.templatereplaceme.client.lib.settings.FrontEndApplicationSettingKey
import com.cramsan.templatereplaceme.client.lib.settings.SettingsHolder
import org.koin.dsl.module

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
