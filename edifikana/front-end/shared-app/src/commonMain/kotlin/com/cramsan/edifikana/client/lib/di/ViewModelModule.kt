package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.features.account.account.AccountViewModel
import com.cramsan.edifikana.client.lib.features.account.changepassword.ChangePasswordDialogViewModel
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsViewModel
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInViewModel
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpViewModel
import com.cramsan.edifikana.client.lib.features.auth.validation.OtpValidationViewModel
import com.cramsan.edifikana.client.lib.features.debug.main.DebugViewModel
import com.cramsan.edifikana.client.lib.features.debug.screenselector.ScreenSelectorViewModel
import com.cramsan.edifikana.client.lib.features.home.addprimaryemployee.AddPrimaryEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.addproperty.AddPropertyViewModel
import com.cramsan.edifikana.client.lib.features.home.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.home.addsecondaryemployee.AddSecondaryEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.drawer.DrawerViewModel
import com.cramsan.edifikana.client.lib.features.home.employee.EmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.employeelist.EmployeeListViewModel
import com.cramsan.edifikana.client.lib.features.home.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.home.gotoorganization.GoToOrganizationViewModel
import com.cramsan.edifikana.client.lib.features.home.organizationhome.OrganizationHomeViewModel
import com.cramsan.edifikana.client.lib.features.home.properties.PropertyManagerViewModel
import com.cramsan.edifikana.client.lib.features.home.property.PropertyViewModel
import com.cramsan.edifikana.client.lib.features.home.propertyhome.PropertyHomeViewModel
import com.cramsan.edifikana.client.lib.features.home.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.home.timecardemployeelist.TimeCardEmployeeListViewModel
import com.cramsan.edifikana.client.lib.features.home.viewemployee.ViewEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {
    viewModelOf(::EventLogViewModel)
    viewModelOf(::TimeCartViewModel)
    viewModelOf(::EmployeeListViewModel)
    viewModelOf(::ViewRecordViewModel)
    viewModelOf(::AddRecordViewModel)
    viewModelOf(::ViewEmployeeViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::PropertyManagerViewModel)
    viewModelOf(::PropertyViewModel)
    viewModelOf(::DebugViewModel)
    viewModelOf(::PropertyHomeViewModel)
    viewModelOf(::OtpValidationViewModel)
    viewModelOf(::AddPropertyViewModel)
    viewModelOf(::OrganizationHomeViewModel)
    viewModelOf(::AddPrimaryEmployeeViewModel)
    viewModelOf(::AddSecondaryEmployeeViewModel)
    viewModelOf(::EmployeeViewModel)
    viewModelOf(::TimeCardEmployeeListViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::DrawerViewModel)
    viewModelOf(::ScreenSelectorViewModel)
    viewModelOf(::ChangePasswordDialogViewModel)
    viewModelOf(::GoToOrganizationViewModel)
}
