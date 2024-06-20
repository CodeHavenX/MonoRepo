package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.managers.AttachmentManager
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.FormsManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig
import com.cramsan.edifikana.client.lib.service.RemoteConfigService
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ManagerModule = module {
    single { WorkContext(get(), get(), get(), get(), get<String>(named("FirebaseStorageBucketName"))) }
    singleOf(::EventLogManager)
    singleOf(::AttachmentManager)
    singleOf(::TimeCardManager)
    singleOf(::EmployeeManager)
    singleOf(::FormsManager)
    singleOf(::AuthManager)
    single { get<RemoteConfigService>().getRemoteConfigPayload() }
    single { get<RemoteConfig>().caching }
    single { get<RemoteConfig>().image }
    single { get<RemoteConfig>().behavior }
    single { get<RemoteConfig>().features }
}
