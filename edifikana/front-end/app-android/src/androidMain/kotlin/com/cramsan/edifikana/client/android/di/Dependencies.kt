package com.cramsan.edifikana.client.android.di

import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import coil.ImageLoader
import com.cramsan.edifikana.client.android.BuildConfig
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.db.AppDatabase
import com.cramsan.edifikana.client.android.db.models.EventLogRecordDao
import com.cramsan.edifikana.client.android.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.android.db.models.TimeCardRecordDao
import com.cramsan.edifikana.client.android.framework.crashhandler.CrashlyticsCrashHandler
import com.cramsan.edifikana.client.android.framework.crashhandler.CrashlyticsErrorCallback
import com.cramsan.edifikana.client.android.managers.remoteconfig.*
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.crashehandler.CrashHandler
import com.cramsan.framework.crashehandler.CrashHandlerDelegate
import com.cramsan.framework.crashehandler.implementation.CrashHandlerImpl
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilAndroid
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.logging.*
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.LoggerAndroid
import com.cramsan.framework.metrics.MetricType
import com.cramsan.framework.metrics.MetricUnit
import com.cramsan.framework.metrics.MetricsDelegate
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilAndroid
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.datetime.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Dependencies {

    // Framework Dependencies

    @Provides
    @Singleton
    fun provideAssertUtil(
        eventLoggerInterface: EventLoggerInterface,
        haltUtilInterface: HaltUtil,
    ): AssertUtilInterface {
        val impl = AssertUtilImpl(
            BuildConfig.DEBUG,
            eventLoggerInterface,
            haltUtilInterface,
        )
        AssertUtil.setInstance(impl)
        return AssertUtil.singleton
    }

    @Provides
    @Singleton
    fun provideThreadUtilDelegate(
        assertUtilInterface: AssertUtilInterface,
    ): ThreadUtilDelegate {
        return ThreadUtilAndroid(assertUtilInterface)
    }

    @Provides
    @Singleton
    fun provideCrashHandlerDelegate(): CrashHandlerDelegate = CrashlyticsCrashHandler()

    @Provides
    @Singleton
    fun provideCrashHandlerInterface(crashHandlerDelegate: CrashHandlerDelegate): CrashHandler =
        CrashHandlerImpl(crashHandlerDelegate)

    @Provides
    @Singleton
    fun provideEventLoggerErrorCallbackDelegate(): EventLoggerErrorCallbackDelegate {
        return CrashlyticsErrorCallback()
    }

    @Provides
    @Singleton
    fun provideEventLoggerErrorCallback(
        eventLoggerDelegate: EventLoggerDelegate,
        delegate: EventLoggerErrorCallbackDelegate,
    ): EventLoggerErrorCallback =
        EventLoggerErrorCallbackImpl(eventLoggerDelegate, delegate)

    @Provides
    @Singleton
    fun provideEventLoggerDelegate(): EventLoggerDelegate = LoggerAndroid()

    @Provides
    @Singleton
    fun provideEventLoggerInterface(
        eventLoggerDelegate: EventLoggerDelegate,
        errorCallback: EventLoggerErrorCallback?,
    ): EventLoggerInterface {
        val severity = if (BuildConfig.DEBUG) {
            Severity.VERBOSE
        } else {
            Severity.INFO
        }
        val instance =
            EventLoggerImpl(
                severity,
                errorCallback,
                eventLoggerDelegate,
            )
        EventLogger.setInstance(instance)
        return EventLogger.singleton
    }

    @Provides
    @Singleton
    fun provideHaltUtilDelegate(@ApplicationContext appContext: Context): HaltUtilDelegate =
        HaltUtilAndroid(appContext)

    @Provides
    @Singleton
    fun provideHaltUtilInterface(haltUtilDelegate: HaltUtilDelegate): HaltUtil =
        HaltUtilImpl(haltUtilDelegate)

    @Provides
    @Singleton
    fun provideThreadUtilInterface(threadUtilDelegate: ThreadUtilDelegate): ThreadUtilInterface {
        val instance = ThreadUtilImpl(threadUtilDelegate)
        ThreadUtil.setInstance(instance)
        return ThreadUtil.singleton
    }

    @Provides
    @Singleton
    fun provideMetricsDelegate(): MetricsDelegate {
        return object : MetricsDelegate {
            override fun initialize() = Unit
            override fun record(
                type: MetricType,
                namespace: String,
                tag: String,
                metadata: Map<String, String>?,
                value: Double,
                unit: MetricUnit
            ) = Unit
        }
    }

    // Firebase Dependencies
    @Singleton
    @Provides
    fun provideFireStore(): FirebaseFirestore {
        val settings = firestoreSettings {
            // Use persistent disk cache (default)
            setLocalCacheSettings(
                persistentCacheSettings {
                }
            )
        }
        Firebase.firestore.firestoreSettings = settings
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Singleton
    @Provides
    @FirebaseStorageBucketName
    fun provideFirebaseBucketName(): String {
        return Firebase.app.options.storageBucket ?: TODO("Add error handling")
    }

    @Singleton
    @Provides
    @FirebaseProjectName
    fun provideFirebaseProjectName(): String {
        return Firebase.app.options.projectId ?: TODO("Add error handling")
    }

    // Application Dependencies

    @Singleton
    @Provides
    fun provideClock(): Clock {
        return Clock.System
    }

    @Singleton
    @Provides
    fun provideExceptionHandler(
        eventLogger: EventLoggerInterface,
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            eventLogger.e("CoroutineExceptionHandler", "Uncaught Exception", throwable)
        }
    }

    @Singleton
    @BackgroundDispatcher
    @Provides
    fun provideBackgroundDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Singleton
    @UIThreadDispatcher
    @Provides
    fun provideUIDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }

    @Singleton
    @Provides
    fun provideResource(
        @ApplicationContext
        context: Context,
    ): Resources {
        return context.resources
    }

    @Singleton
    @Provides
    fun provideAppScope(): CoroutineScope {
        return GlobalScope
    }

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext
        context: Context,
    ): ImageLoader = ImageLoader.Builder(context)
        .respectCacheHeaders(false)
        .build()

    @Singleton
    @Provides
    fun provideRoom(
        @ApplicationContext
        context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "offline-db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // Room Dependencies

    @Singleton
    @Provides
    fun provideEventLogRecordDao(
        appDatabase: AppDatabase,
    ): EventLogRecordDao = appDatabase.eventLogRecordDao()

    @Singleton
    @Provides
    fun provideTimeCardRecordDao(
        appDatabase: AppDatabase,
    ): TimeCardRecordDao = appDatabase.timeCardRecordDao()

    @Singleton
    @Provides
    fun provideFileAttachmentDao(
        appDatabase: AppDatabase,
    ): FileAttachmentDao = appDatabase.fileAttachmentDao()

    // Configuration Dependencies

    @Singleton
    @Provides
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            // The default is 12 hours. If needed we can override it here.
            if (BuildConfig.DEBUG) {
                minimumFetchIntervalInSeconds = 30
            }
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
        return remoteConfig
    }

    @Singleton
    @Provides
    fun provideRemoteConfigService(
        service: RemoteConfigService
    ): RemoteConfig = service.getRemoteConfigPayload()

    @Singleton
    @Provides
    fun provideCachingConfig(remoteConfig: RemoteConfig): CachingConfig = remoteConfig.caching

    @Singleton
    @Provides
    fun provideImageConfig(remoteConfig: RemoteConfig): ImageConfig = remoteConfig.image

    @Singleton
    @Provides
    fun provideBehaviorConfig(remoteConfig: RemoteConfig): BehaviorConfig = remoteConfig.behavior

    @Singleton
    @Provides
    fun provideFeaturesConfig(remoteConfig: RemoteConfig): FeatureConfig = remoteConfig.features
}
