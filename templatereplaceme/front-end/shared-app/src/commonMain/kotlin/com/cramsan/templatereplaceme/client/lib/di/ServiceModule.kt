package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.templatereplaceme.client.lib.managers.UserManager
import com.cramsan.templatereplaceme.client.lib.service.UserService
import com.cramsan.templatereplaceme.client.lib.service.impl.UserServiceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ServiceModule = module {
    singleOf(::UserServiceImpl) {
        bind<UserService>()
    }
}
