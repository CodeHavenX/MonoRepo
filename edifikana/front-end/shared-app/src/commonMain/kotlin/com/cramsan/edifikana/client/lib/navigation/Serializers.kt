package com.cramsan.edifikana.client.lib.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId

/**
 * Custom NavType for PropertyId, allowing it to be passed as a navigation argument.
 */
class PropertyIdNavType : NavType<PropertyId>(isNullableAllowed = false) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: PropertyId
    ) {
        bundle.write {
            this.putString(key, value.propertyId)
        }
    }

    override fun get(
        bundle: SavedState,
        key: String
    ): PropertyId? {
        return bundle.read {
            this.getStringOrNull(key)?.let { parseValue(it) }
        }
    }

    override fun parseValue(value: String): PropertyId {
        return PropertyId(value)
    }
}

/**
 * Custom NavType for UserId, allowing it to be passed as a navigation argument.
 */
class UserIdNavType : NavType<UserId>(isNullableAllowed = false) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: UserId
    ) {
        bundle.write {
            this.putString(key, value.userId)
        }
    }

    override fun get(
        bundle: SavedState,
        key: String
    ): UserId? {
        return bundle.read {
            this.getStringOrNull(key)?.let { parseValue(it) }
        }
    }

    override fun parseValue(value: String): UserId {
        return UserId(value)
    }
}

/**
 * Custom NavType for TimeCardEventId, allowing it to be passed as a navigation argument.
 */
class TimeCardEventIdNavType : NavType<TimeCardEventId>(isNullableAllowed = false) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: TimeCardEventId
    ) {
        bundle.write {
            this.putString(key, value.timeCardEventId)
        }
    }

    override fun get(
        bundle: SavedState,
        key: String
    ): TimeCardEventId? {
        return bundle.read {
            this.getStringOrNull(key)?.let { parseValue(it) }
        }
    }

    override fun parseValue(value: String): TimeCardEventId {
        return TimeCardEventId(value)
    }
}

/**
 * Custom NavType for StaffId, allowing it to be passed as a navigation argument.
 */
class StaffIdNavType : NavType<StaffId>(isNullableAllowed = false) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: com.cramsan.edifikana.lib.model.StaffId
    ) {
        bundle.write {
            this.putString(key, value.staffId)
        }
    }
    override fun get(
        bundle: SavedState,
        key: String
    ): StaffId? {
        return bundle.read {
            this.getStringOrNull(key)?.let { parseValue(it) }
        }
    }
    override fun parseValue(value: String): StaffId {
        return StaffId(value)
    }
}

/**
 * Custom NavType for EventLogEntryId, allowing it to be passed as a navigation argument.
 */
class EventLogEntryIdNavType : NavType<String>(isNullableAllowed = false) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: String
    ) {
        bundle.write {
            this.putString(key, value)
        }
    }

    override fun get(
        bundle: SavedState,
        key: String
    ): String? {
        return bundle.read {
            this.getStringOrNull(key)
        }
    }

    override fun parseValue(value: String): String {
        return value
    }
}