package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.templatereplaceme.client.lib.service.PingPongService
import com.cramsan.templatereplaceme.client.lib.service.impl.PingPongServiceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ServiceModule =
    module {
        singleOf(::PingPongServiceImpl) {
            bind<PingPongService>()
        }
    }
