package com.cramsan.edifikana.client.android.di.koin

import com.cramsan.edifikana.client.android.features.signin.SignInViewModel
import org.koin.dsl.module

val ViewModelPlatformModule = module {

    factory { SignInViewModel(get(), get(), get(), get()) }
}
