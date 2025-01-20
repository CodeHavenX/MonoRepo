package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.managers.AttachmentManager
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.managers.remoteconfig.BehaviorConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.CachingConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.FeatureConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.ImageConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.service.impl.AuthServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.EventLogServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.PropertyServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.StaffServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.StorageServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.TimeCardServiceImpl
import com.cramsan.framework.core.ManagerDependencies
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ManagerModule = module {
    single { ManagerDependencies(get(), get()) }

    singleOf(::EventLogManager)
    singleOf(::AttachmentManager)
    singleOf(::TimeCardManager)
    singleOf(::StaffManager)
    singleOf(::AuthManager)
    singleOf(::PropertyManager)

    single {
        RemoteConfig(
            image = ImageConfig(
                captureWidth = 800,
                captureHeight = 600,
            ),
            caching = CachingConfig(
                imageQualityHint = 80,
            ),
            behavior = BehaviorConfig(
                fetchPeriod = 1,
                allowListedCodes = emptyList(),
            ),
            features = FeatureConfig(
                flags = emptyMap()
            ),
        )
    }
    single { get<RemoteConfig>().caching }
    single { get<RemoteConfig>().image }
    single { get<RemoteConfig>().behavior }
    single { get<RemoteConfig>().features }

    // Services
    singleOf(::AuthServiceImpl) {
        bind<AuthService>()
        bind<AuthServiceImpl>()
    }
    singleOf(::EventLogServiceImpl) {
        bind<EventLogService>()
    }
    singleOf(::PropertyServiceImpl) {
        bind<PropertyService>()
    }
    singleOf(::StorageServiceImpl) {
        bind<StorageService>()
    }
    singleOf(::TimeCardServiceImpl) {
        bind<TimeCardService>()
    }
    singleOf(::StaffServiceImpl) {
        bind<StaffService>()
    }
}

internal const val EDIFIKANA_SUPABASE_URL = "EDIFIKANA_SUPABASE_URL"
internal const val EDIFIKANA_SUPABASE_KEY = "EDIFIKANA_SUPABASE_KEY"
