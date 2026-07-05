package com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the OrgDetail feature.
 */
sealed class OrgDetailEvent : ViewModelEvent {
    /** No operation. */
    data object Noop : OrgDetailEvent()
}
