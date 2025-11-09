package com.cramsan.architecture.server.dependencyinjection

import com.cramsan.architecture.server.settings.BackEndApplicationSettingKey
import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.configuration.ConfigurationMultiplexer
import com.cramsan.framework.configuration.EnvironmentConfiguration
import com.cramsan.framework.configuration.SimpleConfiguration
import com.cramsan.framework.core.BEDispatcherProvider
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.*
import com.cramsan.framework.logging.implementation.*
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.JVMPreferencesDelegate
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import org.apache.logging.log4j.Logger
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the framework level components.
 */
val FrameworkModule = module(createdAtStart = true) {
    single<PreferencesDelegate> { JVMPreferencesDelegate("templatereplaceme-backend") }

    single<Preferences> { PreferencesImpl(get()) }

    single {
        val settingsHolder: SettingsHolder = get()
        val settingRawString = settingsHolder.getString(BackEndApplicationSettingKey.LoggingLevel)
        Severity.fromStringOrDefault(settingRawString, Severity.DEBUG)
    }

    single<Logger> {
        val settingsHolder: SettingsHolder = get()
        val enableFileLogging = settingsHolder.getBoolean(BackEndApplicationSettingKey.LoggingEnableFileLogging)

        Log4J2Helpers.getRootLogger(enableFileLogging ?: false, get())
    }

    single<EventLoggerDelegate> { LoggerJVM(get(), get()) }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(
            get(),
            NoopEventLoggerErrorCallbackDelegate(),
        )
    }

    single<EventLoggerInterface> {
        val severity: Severity = get()
        val instance = EventLoggerImpl(severity, get(), get())
        EventLogger.setInstance(instance)
        EventLogger.singleton
    }

    single<HaltUtilDelegate> { HaltUtilJVM(get()) }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<AssertUtilInterface> {
        val settingsHolder: SettingsHolder = get()
        val haltOnFailure = settingsHolder.getBoolean(BackEndApplicationSettingKey.HaltOnFailure)

        val impl = AssertUtilImpl(
            haltOnFailure ?: false,
            get(),
            get(),
        )
        AssertUtil.setInstance(impl)
        AssertUtil.singleton
    }

    single<ThreadUtilDelegate> { ThreadUtilJVM(get(), get()) }

    single<ThreadUtilInterface> {
        ThreadUtilImpl(get())
    }

    single<DispatcherProvider> { BEDispatcherProvider() }

    single<SimpleConfiguration> {
        val stageSegment: String = get(named(NamedDependency.STAGE_KEY))
        val fileName = if (stageSegment.isBlank()) {
            "config.properties"
        } else {
            "config.properties.$stageSegment"
        }
        SimpleConfiguration(fileName)
    }

    single<EnvironmentConfiguration> { EnvironmentConfiguration("TEMPLATE_REPLACE_ME") }

    single {
        val configurationMultiplexer = ConfigurationMultiplexer()
        val simpleConfiguration: SimpleConfiguration = get()
        val environmentConfiguration: EnvironmentConfiguration = get()
        configurationMultiplexer.setConfigurations(
            listOf(
                environmentConfiguration, // Look for overrides in environment variables first
                simpleConfiguration, // Then in the config file
            )
        )
        configurationMultiplexer
    }
}
