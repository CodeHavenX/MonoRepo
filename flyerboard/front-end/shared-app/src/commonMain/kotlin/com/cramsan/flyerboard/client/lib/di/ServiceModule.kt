package com.cramsan.flyerboard.client.lib.di

import com.cramsan.flyerboard.client.lib.service.UserService
import com.cramsan.flyerboard.client.lib.service.impl.UserServiceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ServiceModule = module {
    singleOf(::UserServiceImpl) {
        bind<UserService>()
    }
}
