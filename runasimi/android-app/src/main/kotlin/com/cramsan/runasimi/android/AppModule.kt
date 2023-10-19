package com.cramsan.runasimi.android

import android.content.Context
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.DispatcherProviderImpl
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.LoggerAndroid
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.implementation.PreferencesAndroid
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.runasimi.mpplib.AndroidFileManager
import com.cramsan.runasimi.mpplib.AndroidFileReader
import com.cramsan.runasimi.mpplib.AndroidSoundManager
import com.cramsan.runasimi.mpplib.FileReader
import com.cramsan.runasimi.mpplib.StatementManager
import com.cramsan.runasimi.mpplib.VerbProvider
import com.cramsan.runasimi.mpplib.main.FileManager
import com.cramsan.runasimi.mpplib.main.MainViewModel
import com.cramsan.runasimi.mpplib.main.SoundManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideEventLogger(): EventLoggerInterface {
        val eventLogger = EventLoggerImpl(
            targetSeverity = Severity.DEBUG,
            errorCallback = null,
            platformDelegate = LoggerAndroid(),
        )
        EventLogger.setInstance(eventLogger)
        return eventLogger
    }

    @Provides
    fun providePreferences(
        @ApplicationContext appContext: Context,
    ): Preferences {
        return PreferencesImpl(PreferencesAndroid(appContext))
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    fun provideApplicationScope(): CoroutineScope {
        return GlobalScope
    }

    @Provides
    fun provideSoundManager(scope: CoroutineScope): SoundManager {
        return AndroidSoundManager(scope)
    }

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }

    @Provides
    fun provideFileManager(
        @ApplicationContext appContext: Context,
    ): FileManager {
        return AndroidFileManager(appContext)
    }

    @Provides
    fun provideFileReader(): FileReader {
        return AndroidFileReader()
    }

    @Provides
    fun provideVerbProvider(
        fileReader: FileReader,
    ): VerbProvider {
        return VerbProvider(
            fileReader
        )
    }

    @Provides
    fun provideStatementManager(
        verbProvider: VerbProvider,
    ): StatementManager {
        return StatementManager(
            verbProvider
        )
    }

    @Provides
    fun provideMainViewModel(
        statementManager: StatementManager,
        httpClient: HttpClient,
        preferences: Preferences,
        soundManager: SoundManager,
        fileManager: FileManager,
        dispatcherProvider: DispatcherProvider,
    ): MainViewModel {
        return MainViewModel(
            statementManager,
            httpClient,
            preferences,
            soundManager,
            fileManager,
            dispatcherProvider,
        )
    }

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient()
    }
}
