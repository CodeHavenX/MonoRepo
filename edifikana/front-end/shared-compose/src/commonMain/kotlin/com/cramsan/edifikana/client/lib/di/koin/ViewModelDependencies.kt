package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.account.AccountActivityViewModel
import com.cramsan.edifikana.client.lib.features.root.account.account.AccountViewModel
import com.cramsan.edifikana.client.lib.features.root.admin.AdminActivityViewModel
import com.cramsan.edifikana.client.lib.features.root.admin.properties.PropertyManagerViewModel
import com.cramsan.edifikana.client.lib.features.root.admin.property.PropertyViewModel
import com.cramsan.edifikana.client.lib.features.root.auth.AuthActivityViewModel
import com.cramsan.edifikana.client.lib.features.root.auth.signinv2.SignInV2ViewModel
import com.cramsan.edifikana.client.lib.features.root.auth.signup.SignUpViewModel
import com.cramsan.edifikana.client.lib.features.root.debug.DebugActivityViewModel
import com.cramsan.edifikana.client.lib.features.root.debug.main.DebugViewModel
import com.cramsan.edifikana.client.lib.features.root.main.MainActivityViewModel
import com.cramsan.edifikana.client.lib.features.root.main.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.root.main.eventlog.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.root.main.eventlog.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.root.main.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.root.main.timecard.addstaff.AddStaffViewModel
import com.cramsan.edifikana.client.lib.features.root.main.timecard.stafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.root.main.timecard.viewstaff.ViewStaffViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ViewModelModule = module {

    singleOf(::ViewModelDependencies)

    // TODO: Currently we cannot scope a viewmodel to a navigation graph until koin supports the viewModel function
    // in compose multiplatform code. Until then we will have all viewmodels as singletons.
    singleOf(::EventLogViewModel)
    singleOf(::TimeCartViewModel)
    singleOf(::StaffListViewModel)
    singleOf(::ViewRecordViewModel)
    singleOf(::AddStaffViewModel)
    singleOf(::AddRecordViewModel)
    singleOf(::ViewStaffViewModel)
    singleOf(::MainActivityViewModel)
    singleOf(::SignInV2ViewModel)
    singleOf(::SignUpViewModel)
    singleOf(::AuthActivityViewModel)
    singleOf(::AccountActivityViewModel)
    singleOf(::AccountViewModel)
    singleOf(::AdminActivityViewModel)
    singleOf(::PropertyManagerViewModel)
    singleOf(::PropertyViewModel)
    singleOf(::EdifikanaApplicationViewModel)
    singleOf(::DebugActivityViewModel)
    singleOf(::DebugViewModel)
}
