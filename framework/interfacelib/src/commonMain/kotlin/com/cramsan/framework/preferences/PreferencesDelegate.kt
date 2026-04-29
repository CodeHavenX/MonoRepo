package com.cramsan.framework.preferences

/**
 * Platform delegate that implements the logic to store the values.
 * Extends [Preferences] so that platform implementations can be used directly
 * wherever [Preferences] is required, without a wrapping [PreferencesImpl].
 */
interface PreferencesDelegate : Preferences
