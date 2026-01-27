package com.cramsan.edifikana.client.lib.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId

/**
 * Custom NavType for PropertyId, allowing it to be passed as a navigation argument.
 */
class PropertyIdNavType : NavType<PropertyId>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: PropertyId) {
        bundle.write {
            this.putString(key, value.propertyId)
        }
    }

    override fun get(bundle: SavedState, key: String): PropertyId? = bundle.read {
        this.getStringOrNull(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): PropertyId = PropertyId(value)
}

/**
 * Custom NavType for UserId, allowing it to be passed as a navigation argument.
 */
class UserIdNavType : NavType<UserId>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: UserId) {
        bundle.write {
            this.putString(key, value.userId)
        }
    }

    override fun get(bundle: SavedState, key: String): UserId? = bundle.read {
        this.getStringOrNull(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): UserId = UserId(value)
}

/**
 * Custom NavType for TimeCardEventId, allowing it to be passed as a navigation argument.
 */
class TimeCardEventIdNavType : NavType<TimeCardEventId>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: TimeCardEventId) {
        bundle.write {
            this.putString(key, value.timeCardEventId)
        }
    }

    override fun get(bundle: SavedState, key: String): TimeCardEventId? = bundle.read {
        this.getStringOrNull(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): TimeCardEventId = TimeCardEventId(value)
}

/**
 * Custom NavType for EmployeeId, allowing it to be passed as a navigation argument.
 */
class EmployeeIdNavType : NavType<EmployeeId>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: com.cramsan.edifikana.lib.model.EmployeeId) {
        bundle.write {
            this.putString(key, value.empId)
        }
    }
    override fun get(bundle: SavedState, key: String): EmployeeId? = bundle.read {
        this.getStringOrNull(key)?.let { parseValue(it) }
    }
    override fun parseValue(value: String): EmployeeId = EmployeeId(value)
}

/**
 * Custom NavType for EventLogEntryId, allowing it to be passed as a navigation argument.
 */
class EventLogEntryIdNavType : NavType<String>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: String) {
        bundle.write {
            this.putString(key, value)
        }
    }

    override fun get(bundle: SavedState, key: String): String? = bundle.read {
        this.getStringOrNull(key)
    }

    override fun parseValue(value: String): String = value
}

/**
 * Custom NavType for OrganizationId, allowing it to be passed as a navigation argument.
 */
class OrganizationIdNavType : NavType<OrganizationId>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: OrganizationId) {
        bundle.write {
            this.putString(key, value.id)
        }
    }

    override fun get(bundle: SavedState, key: String): OrganizationId? = bundle.read {
        this.getStringOrNull(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): OrganizationId = OrganizationId(value)
}
