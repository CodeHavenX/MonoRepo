package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.features.account.account.AccountViewModel
import com.cramsan.edifikana.client.lib.features.account.changepassword.ChangePasswordDialogViewModel
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsViewModel
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInViewModel
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpViewModel
import com.cramsan.edifikana.client.lib.features.auth.validation.OtpValidationViewModel
import com.cramsan.edifikana.client.lib.features.debug.main.DebugViewModel
import com.cramsan.edifikana.client.lib.features.debug.screenselector.ScreenSelectorViewModel
import com.cramsan.edifikana.client.lib.features.home.addproperty.AddPropertyViewModel
import com.cramsan.edifikana.client.lib.features.home.drawer.DrawerViewModel
import com.cramsan.edifikana.client.lib.features.home.gotoorganization.GoToOrganizationViewModel
import com.cramsan.edifikana.client.lib.features.home.organizationhome.OrganizationHomeViewModel
import com.cramsan.edifikana.client.lib.features.home.propertiesoverview.PropertiesOverviewViewModel
import com.cramsan.edifikana.client.lib.features.home.propertyhome.PropertyHomeViewModel
import com.cramsan.edifikana.client.lib.features.settings.general.SettingsViewModel
import com.cramsan.edifikana.client.lib.features.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::DebugViewModel)
    viewModelOf(::PropertyHomeViewModel)
    viewModelOf(::OtpValidationViewModel)
    viewModelOf(::AddPropertyViewModel)
    viewModelOf(::OrganizationHomeViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::DrawerViewModel)
    viewModelOf(::ScreenSelectorViewModel)
    viewModelOf(::ChangePasswordDialogViewModel)
    viewModelOf(::GoToOrganizationViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::PropertiesOverviewViewModel)
}
