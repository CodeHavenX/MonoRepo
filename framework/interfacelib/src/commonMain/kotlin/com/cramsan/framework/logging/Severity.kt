package com.cramsan.framework.logging

/**
 * Severity used for logging purposes.
 */
enum class Severity {
    /**
     * Log everything. This is only to be used in extreme cases. It can easily produce too much
     * output.
     */
    VERBOSE,

    /**
     * Log some extra information to help with debugging problems.
     */
    DEBUG,

    /**
     * Regular log level.
     */
    INFO,

    /**
     * Only log potentially problematic situations.
     */
    WARNING,

    /**
     * Log problems that directly impact functionality.
     */
    ERROR,

    /**
     * Do not log anything.
     */
    DISABLED,
    ;

    companion object {

        /**
         * Get a [Severity] from a string. If the string is not a valid [Severity], the [default] value
         * is returned.
         */
        fun fromStringOrDefault(value: String?, default: Severity = DEBUG): Severity = if (value == null) {
            default
        } else {
            entries.find { it.name.lowercase() == value.lowercase() } ?: default
        }
    }
}
