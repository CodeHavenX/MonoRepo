package com.cramsan.edifikana.client.desktop.koin

import android.app.Application
import com.cramsan.edifikana.client.desktop.service.FirebaseAuthService
import com.cramsan.edifikana.client.desktop.service.FirebaseEmployeeService
import com.cramsan.edifikana.client.desktop.service.FirebaseEventLogService
import com.cramsan.edifikana.client.desktop.service.FirebaseFormsService
import com.cramsan.edifikana.client.desktop.service.FirebasePropertyConfigService
import com.cramsan.edifikana.client.desktop.service.FirebaseRemoteConfigService
import com.cramsan.edifikana.client.desktop.service.FirebaseStorageService
import com.cramsan.edifikana.client.desktop.service.FirebaseTimeCardService
import com.cramsan.edifikana.client.desktop.service.JvmFirebasePlatform
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.FormsService
import com.cramsan.edifikana.client.lib.service.PropertyConfigService
import com.cramsan.edifikana.client.lib.service.RemoteConfigService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import dev.gitlive.firebase.remoteconfig.remoteConfig
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ManagerPlatformModule = module {

    single<FirebasePlatform> {
        JvmFirebasePlatform(get(), get())
    }

    single(createdAtStart = true) {
        FirebasePlatform.initializeFirebasePlatform(get())
        val options = FirebaseOptions(
            projectId = "edifikana-stage",
            applicationId = "1:225276838088:android:0499ce5edcda27d98325fd",
            apiKey = "AIzaSyBi5To1Eptzplixc_McDzeohNc77KXXPvQ",
            storageBucket = "edifikana-stage.appspot.com",
        )
        Firebase.initialize(Application(), options)
    }

    single { Firebase.firestore }
    single {
        Firebase.storage
    }
    single { Firebase.auth }
    single<String>(named("FirebaseStorageBucketName")) {
        Firebase.app.options.storageBucket ?: TODO(
            "Add error handling"
        )
    }
    single<AuthService> { FirebaseAuthService(get(), get()) }
    single<EventLogService> { FirebaseEventLogService(get(), get()) }
    single<FormsService> { FirebaseFormsService(get(), get()) }
    single<PropertyConfigService> { FirebasePropertyConfigService(get()) }
    single<StorageService> { FirebaseStorageService(get()) }
    single<TimeCardService> { FirebaseTimeCardService(get(), get()) }
    single<EmployeeService> { FirebaseEmployeeService(get()) }
    single<RemoteConfigService> { FirebaseRemoteConfigService(get()) }

    single {
        Firebase.remoteConfig.apply {
            get<CoroutineScope>().launch {
                fetchAndActivate()
            }
        }
    }

    singleOf(::IODependencies)
}
