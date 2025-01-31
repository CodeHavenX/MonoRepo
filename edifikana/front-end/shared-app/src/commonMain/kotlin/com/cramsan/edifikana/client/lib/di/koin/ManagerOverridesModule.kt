package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.service.dummy.DummyEventLogService
import com.cramsan.edifikana.client.lib.service.dummy.DummyPropertyService
import com.cramsan.edifikana.client.lib.service.dummy.DummyStaffService
import com.cramsan.edifikana.client.lib.service.dummy.DummyTimeCardService
import com.cramsan.edifikana.client.lib.service.impl.EventLogServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.PropertyServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.StaffServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.TimeCardServiceImpl
import com.cramsan.edifikana.client.lib.settings.Overrides
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ManagerOverridesModule = module {

    single<EventLogService> {
        val disableBackEnd = get<Boolean>(named(Overrides.KEY_DISABLE_BE))
        if (disableBackEnd) {
            DummyEventLogService()
        } else {
            get<EventLogServiceImpl>()
        }
    }

    single<PropertyService> {
        val disableBackEnd = get<Boolean>(named(Overrides.KEY_DISABLE_BE))
        if (disableBackEnd) {
            DummyPropertyService()
        } else {
            get<PropertyServiceImpl>()
        }
    }

    single<TimeCardService> {
        val disableBackEnd = get<Boolean>(named(Overrides.KEY_DISABLE_BE))
        if (disableBackEnd) {
            DummyTimeCardService()
        } else {
            get<TimeCardServiceImpl>()
        }
    }

    single<StaffService> {
        val disableBackEnd = get<Boolean>(named(Overrides.KEY_DISABLE_BE))
        if (disableBackEnd) {
            DummyStaffService()
        } else {
            get<StaffServiceImpl>()
        }
    }
}
