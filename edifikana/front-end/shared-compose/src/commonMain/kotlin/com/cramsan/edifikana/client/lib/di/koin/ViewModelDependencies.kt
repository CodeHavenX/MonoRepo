package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.eventlog.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityViewModel
import com.cramsan.edifikana.client.lib.features.signinv2.SignInV2ViewModel
import com.cramsan.edifikana.client.lib.features.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.timecard.addstaff.AddStaffViewModel
import com.cramsan.edifikana.client.lib.features.timecard.stafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.timecard.viewstaff.ViewStaffViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ViewModelModule = module {

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
}
