package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.managers.PreferencesManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ManagerModule = module {
    singleOf(::EventLogManager)
    singleOf(::TimeCardManager)
    singleOf(::EmployeeManager)
    singleOf(::AuthManager)
    singleOf(::PropertyManager)
    singleOf(::PreferencesManager)
    singleOf(::OrganizationManager)
}
