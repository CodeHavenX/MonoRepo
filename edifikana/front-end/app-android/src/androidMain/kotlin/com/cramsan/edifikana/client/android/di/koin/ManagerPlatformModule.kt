package com.cramsan.edifikana.client.android.di.koin

import com.cramsan.edifikana.client.android.BuildConfig
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.service.FirebaseAuthService
import com.cramsan.edifikana.client.android.service.FirebaseEmployeeService
import com.cramsan.edifikana.client.android.service.FirebaseEventLogService
import com.cramsan.edifikana.client.android.service.FirebaseFormsService
import com.cramsan.edifikana.client.android.service.FirebasePropertyConfigService
import com.cramsan.edifikana.client.android.service.FirebaseRemoteConfigService
import com.cramsan.edifikana.client.android.service.FirebaseStorageService
import com.cramsan.edifikana.client.android.service.FirebaseTimeCardService
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.FormsService
import com.cramsan.edifikana.client.lib.service.PropertyConfigService
import com.cramsan.edifikana.client.lib.service.RemoteConfigService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.firebase.storage.storage
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ManagerPlatformModule = module {

    single {
        Firebase.firestore.apply {
            firestoreSettings = firestoreSettings {
                setLocalCacheSettings(persistentCacheSettings { })
            }
        }
    }
    single { Firebase.storage }
    single { Firebase.auth }
    single<String>(named("FirebaseStorageBucketName")) {
        Firebase.app.options.storageBucket ?: TODO(
            "Add error handling"
        )
    }
    singleOf(::FirebaseAuthService) {
        bind<AuthService>()
    }
    singleOf(::FirebaseEventLogService) {
        bind<EventLogService>()
    }
    singleOf(::FirebaseFormsService) {
        bind<FormsService>()
    }
    singleOf(::FirebasePropertyConfigService) {
        bind<PropertyConfigService>()
    }
    singleOf(::FirebaseStorageService) {
        bind<StorageService>()
    }
    singleOf(::FirebaseTimeCardService) {
        bind<TimeCardService>()
    }
    singleOf(::FirebaseEmployeeService) {
        bind<EmployeeService>()
    }
    singleOf(::FirebaseRemoteConfigService) {
        bind<RemoteConfigService>()
    }

    single {
        Firebase.remoteConfig.apply {
            val configSettings = remoteConfigSettings {
                if (BuildConfig.DEBUG) {
                    minimumFetchIntervalInSeconds = DEBUG_FIREBASE_FETCH_INTERVAL_SECONDS
                }
            }
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate()
        }
    }

    singleOf(::IODependencies)
}

private const val DEBUG_FIREBASE_FETCH_INTERVAL_SECONDS = 30L
