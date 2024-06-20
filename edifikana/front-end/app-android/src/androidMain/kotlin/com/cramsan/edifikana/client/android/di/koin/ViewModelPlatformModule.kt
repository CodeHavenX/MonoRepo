package com.cramsan.edifikana.client.android.di.koin

import com.cramsan.edifikana.client.android.features.signin.SignInViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val ViewModelPlatformModule = module {

    factoryOf(::SignInViewModel)
}
